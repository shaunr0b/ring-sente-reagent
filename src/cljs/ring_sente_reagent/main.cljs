(ns ring-sente-reagent.main
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop alt!)])
  (:require
   ;; <other stuff>
   [cljs.core.async :refer (put! take! <! >! chan)]
   [goog.dom :as dom]
   [goog.events :as events]
   [taoensso.sente :as sente :refer (cb-success?)])
  (:import (goog.events EventType)))

(defn hello []
  (.log js/console "Hello, World!"))

(defn set-clear-handler [channel]
  (events/listen
   (dom/getElement "chat-clear")
   EventType/CLICK
   (fn [event]
     (put! channel :clear))))

(defn set-send-handler [channel]
  (events/listen
   (dom/getElement "chat-send")
   EventType/CLICK
   (fn [event]
     (let [message (.-value (dom/getElement "chat-message"))]
       (put! channel message)))))

(defn print-message [message]
  (let [transcript (dom/getElement "chat-transcript")
        new-message (dom/createDom "p" "" message)]
    (dom/append transcript new-message)))

(defn clear-transcript []
  (dom/removeChildren (dom/getElement "chat-transcript")))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" ; Note the same path as before
       {:type :auto ; e/o #{:auto :ajax :ws}
       })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )


;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn ^:export main []
  (go-loop []
     (.log js/console (str (<! ch-chsk)))
     (recur)))


; (chsk-send! [:some/event {:data "hello from client->server"}])
