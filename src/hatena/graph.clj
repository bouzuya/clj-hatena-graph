(ns hatena.graph
  (:require [clj-time.local :as tl]
            [clj-time.format :as tf]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [clojure.data.codec.base64 :as base64]))

(def ^:dynamic *auth* {})

(defn- random-bytes
  [length]
  (let [b (byte-array length)]
    (.nextBytes (java.util.Random.) b)
    b))

(defn- join-byte-array
  [& byte-arrays]
  (with-open [buf (java.io.ByteArrayOutputStream.)]
    (doseq [ba byte-arrays]
      (.write buf ba 0 (alength ba)))
    (.toByteArray buf)))

(defn- sha1
  [b]
  (.digest (java.security.MessageDigest/getInstance "sha1") b))

(defn- wsse-header
  [username password]
  (let [nonce (random-bytes 20)
        created (tf/unparse (tf/formatters :date-time-no-ms) (tl/local-now))
        digest (sha1 (join-byte-array nonce (.getBytes created) (.getBytes password)))]
    (str "UsernameToken "
         "Username=\"" username "\", "
         "PasswordDigest=\"" (String. (base64/encode digest)) "\", "
         "Nonce=\"" (String. (base64/encode nonce)) "\", "
         "Created=\"" created "\", ")))

(defn- call-api
  [method endpoint params]
  (let [m (case method :get http/get :post http/post)
        k (case method :get :query-params :post :form-params)
        e (case endpoint
            :data "http://graph.hatena.ne.jp/api/data"
            :config "http://graph.hatena.ne.jp/api/config")]
    (m e {k (into {} (remove (comp nil? val) params))
          :headers {"X-WSSE" (wsse-header
                               (*auth* :username)
                               (*auth* :password))}})))

(defn post-data
  ([graphname value] (post-data graphname nil value))
  ([graphname date value]
   (let [response (call-api :post :data {:graphname graphname
                                         :date date
                                         :value value})]
     (= 201 (response :status)))))

(defn get-data
  ([graphname] (get-data graphname nil))
  ([graphname username]
   (let [response (call-api :get :data {:graphname graphname
                                        :username username
                                        :type "json"})]
     (when (= 200 (response :status))
       (json/read-str (response :body))))))

(defn post-config
  [graphname & {:as opts}]
  (let [defaults {:reverse "0"
                  :userlinecolor "CE2418"
                  :formula nil
                  :showdata "1"
                  :miny nil
                  :graphtype "lines"
                  :stack "0"
                  :status "public"
                  :nolabel "0"
                  :allowgrouplist nil
                  :allowuser nil
                  :graphcolor "5279E7"
                  :maxy nil
                  :userline nil
                  :comment nil}
        response (call-api :post :config (merge defaults
                                              {:graphname graphname}
                                              opts))]
    (= 201 (response :status))))

(defn get-config
  [graphname]
  (let [response (call-api :get :config {:graphname graphname :type "json"})]
    (when (= 200 (response :status))
      (json/read-str (response :body)))))

(defmacro with-auth
  [username password & body]
  `(binding [*auth* {:username ~username
                     :password ~password}]
     ~@body))

(defn -main
  [& args]
  (with-auth
    (System/getenv "HATENA_USERNAME")
    (System/getenv "HATENA_PASSWORD")
    (let [graphname (first args)]
      (doseq [line (line-seq (java.io.BufferedReader. *in*))]
        (let [[date value] (.split line ",")]
          (when (and date value)
            (post-data graphname date value)))))))

