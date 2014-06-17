(ns ring-sente-reagent.server
  (:require [cemerick.austin.repls          :refer (browser-connected-repl-js)]
            [compojure.route                :refer (resources not-found)]
            [compojure.core                 :refer (GET POST ANY defroutes routes)]
            [compojure.handler              :as    handler]
            [ring.middleware.anti-forgery   :refer (wrap-anti-forgery)]
            [org.httpkit.server             :refer (run-server)]
            [clojure.java.io                :as    io]
            [taoensso.sente                 :as    sente]))

(defn- logf [fmt & xs] (println (apply format fmt xs)))

(let [{:keys [ch-recv
              send-fn
              ajax-post-fn
              ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv)        ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn)        ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
)

(defroutes static-routes
  (resources "/"))

(defroutes comm-routes
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req)))

(defroutes error-routes
  (not-found "HTTP 404 NOT FOUND // ring-sente-reagent.server"))

(defn- event-msg-handler
  [{:as ev-msg :keys [ring-req event ?reply-fn]} _]
  (let [session (:session ring-req)
        uid     (:uid session)
        [id data :as ev] event]

    (logf "Event: %s" ev)))

(def chsk-router
  (sente/start-chsk-router-loop! event-msg-handler ch-chsk))

(defn catch-all [handler]
  (fn [request]
    (try (handler request)
         (catch Throwable t
           {:status 500
            :body (with-out-str
                    (.printStackTrace t (java.io.PrintWriter. *out*)))}))))
(def app
  (-> (routes
         static-routes
         comm-routes ;; TODO secure me
         error-routes)
      catch-all
      (wrap-anti-forgery
         {:read-token (fn [req] (-> req :params :csrf-token))})
      (handler/site))) ; http://weavejester.github.io/compojure/compojure.handler.html
;;;;;;;;;;;;;;;;;;;;;

(def server nil)

(defn start []
  (alter-var-root
   #'server
   (constantly
    (run-server #'app {:port 8080}))))

(defn stop []
  (alter-var-root #'server #(when % (.stop %))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; (chsk-send! nil [:some/event {:data "hello from server->client"}])
