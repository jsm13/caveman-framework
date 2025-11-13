(ns sandbox.routes
  (:require [clojure.tools.logging :as log]
            [sandbox.system :as-alias system]
            [hiccup2.core :as hiccup]
            [reitit.ring :as reitit-ring]
            [sandbox.hello.routes :as hello-routes]
            [sandbox.goodbye.routes :as goodbye-routes]
            [sandbox.cave.routes :as cave-routes]))

(defn routes
  [system]
  [""
   (hello-routes/routes system)
   (goodbye-routes/routes system)
   (cave-routes/routes system)])

(defn not-found-handler
  [_request]
  {:status 404
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (str
          (hiccup/html
           [:html
            [:body
             [:h1 "Not found"]]]))})

(defn root-handler
  ([system request]
   ((root-handler system) request))
  ([system] 
   (let [handler (reitit-ring/ring-handler
                  (reitit-ring/router
                   (routes system))
                  #'not-found-handler)]
     (fn root-handler [request]
       (log/info (str (:request-method request) " - " (:uri request)))
       (handler request)))))