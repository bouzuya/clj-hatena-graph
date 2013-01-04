# Hatena::Graph API for Clojure

Hatena::Graph API wrapper for Clojure.

## Installation

`clj-hatena-graph` is available as a Maven artifact from
[Clojars](http://clojars.org/org.clojars.bouzuya/hatena.graph).

```clojure
[org.clojars.bouzuya/hatena.graph "0.1.0"]
```

## Usage

```clojure
(ns your-app.core
  (:require [hatena.graph :as graph))

(binding [graph/*auth* {:username "user"
                        :password "pass"}]
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
