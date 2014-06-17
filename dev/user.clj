(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer (javadoc)]
   [clojure.pprint :refer (pprint)]
   [clojure.reflect :refer (reflect)]
   [clojure.repl :refer (apropos dir doc find-doc pst source)]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [clojure.core.async :as async :refer (chan >! <! >!! <!! go alt! alt!! alts! alts!!)]
   [cemerick.austin.repls]
   [cemerick.austin]
   [ring-sente-reagent.server :as server]))

(defn start []
  (reset! cemerick.austin.repls/browser-repl-env
          (cemerick.austin/repl-env))
  (server/start))

(defn stop []
  (server/stop))

(defn reset []
  (stop)
  (refresh :after 'user/start))

(defn cljs-repl []
  (cemerick.austin.repls/cljs-repl @cemerick.austin.repls/browser-repl-env))
