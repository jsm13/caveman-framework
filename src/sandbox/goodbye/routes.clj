(ns sandbox.goodbye.routes
  (:require [hiccup2.core :as hiccup]
            [sandbox.page-html.core :as page-html]))

(defn goodbye-handler
  [_system _request]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (str
          (hiccup/html
           (page-html/view :body
                           [:h1 "Goodbye, everybody"]
                           :title
                           "gbye")))})

(defn routes
  [system]
  [["/goodbye" {:get {:handler (partial goodbye-handler system)}}]])