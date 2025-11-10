(ns sandbox.goodbye.routes
  (:require [hiccup2.core :as hiccup]))

(defn goodbye-handler
  [_system _request]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (str
          (hiccup/html
           [:html
            [:body
             [:h1 "Goodbye, everybody"]]]))})

(defn routes
  [system]
  [["/goodbye" {:get {:handler (partial goodbye-handler system)}}]])