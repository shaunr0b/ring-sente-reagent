(defproject ring-sente-reagent "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2030"]
                 [org.clojure/core.async "0.1.256.0-1bf8cf-alpha"]
                 [com.stuartsierra/component "0.1.0"]
                 [domina "1.0.2"]
                 [enlive "1.1.4"]
                 [enfocus "2.0.1"]
                 [http-kit "2.1.16"]
                 [ring "1.2.1"]
                 [ring/ring-anti-forgery "0.3.1"]
                 [compojure "1.1.6"]
                 [reagent "0.4.2"]
                 [com.taoensso/sente "0.14.1"]]
  :plugins [[lein-cljsbuild "1.0.0-alpha2"]
            [com.cemerick/austin "0.1.3"]]
  :local-repo "repository"
  :source-paths ["src/clj" "src/cljs"]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/javascript/main-dev.js"
                                   :output-dir "resources/public/javascript/"
                                   :optimizations :none
                                   :pretty-print true
                                   :source-map true}}
                       {:id "prod"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/javascript/main-prod.js"
                                   :output-dir "target/tmp/javascript"
                                   :optimizations :advanced
                                   :pretty-print false
                                   :source-map "resources/public/javascript/main-prod.js.map"}}]}
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]]}})
