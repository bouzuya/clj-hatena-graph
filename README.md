# Hatena::Graph API for Clojure

Hatena::Graph API wrapper for Clojure.

## Installation

`clj-hatena-graph` is available as a Maven artifact from
[Clojars](http://clojars.org/org.clojars.bouzuya/hatena.graph).

```clojure
[org.clojars.bouzuya/hatena.graph "0.2.0"]
```

## Usage

in your application:

```clojure
(ns your-app.core
  (:require [hatena.graph :as graph))

(graph/with-auth "hatena-username" "hatena-password"
   (graph/get-data "graphname"))
```

## NOTE

To run the test, you need to create `login.clj`.

```clojure
{:username "hatena-username"
 :password "hatena-password"}
```

## License

Copyright © 2013 bouzuya

Distributed under the Eclipse Public License, the same as Clojure.
