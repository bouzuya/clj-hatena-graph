(ns hatena.graph-test
  (:require [clj-time.local :as tl]
            [clj-time.format :as tf])
  (:use clojure.test
        hatena.graph))

(defn get-auth
  []
  (load-file "login.clj"))

(deftest config-test
  (testing "endpoint config GET/POST"
    (binding [*auth* (get-auth)]
      (let [test-data {:graphcolor "FFFFFF"
                       :graphtype "bars"
                       :status "private"
                       :allowuser "bouzuya"
                       :allowgrouplist "bouzuya"
                       :stack "1"
                       :reverse "1"
                       :formula nil
                       :maxy "100.00"
                       :miny "50.00"
                       :showdata "0"
                       :nolabel "1"
                       :userline "75.00"
                       :userlinecolor "CE2418"
                       :comment "こんにちはこんにちは"}]
        (is (= (apply post-config (concat ["test"] (flatten (seq test-data)))) true))
        (is (= (into {} (map (fn [[k v]] [(keyword k) v]) (get-config "test")))
               test-data))))))

(deftest data-test
  (testing "endpoint data GET/POST"
    (binding [*auth* (get-auth)]
      (is (= (post-data "test" "4") true))
      (is (= (post-data "test" "2013-01-02" "5") true))
      (let [today (tf/unparse (tf/formatter "yyyy-MM-dd") (tl/local-now))
            test-data (get-data "test")]
        (is (= (test-data today) "4.00"))
        (is (= (test-data "2013-01-02") "5.00"))))))

