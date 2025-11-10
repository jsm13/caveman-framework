(ns sandbox.routes
  (:require [clojure.tools.logging :as log]
            [sandbox.system :as-alias system]
            [hiccup2.core :as hiccup]
            [next.jdbc :as jdbc]
            [reitit.ring :as reitit-ring]))

(defn hello-handler
  [{::system/keys [db]} _request]
  (let [{:keys [planet]} (jdbc/execute-one!
                          db
                          ["SELECT 'earth' as planet"])]
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (str
            (hiccup/html
             [:html
              [:body
               [:h1 (str "Hello, " planet)]]]))}))

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
  [["/"        {:get {:handler (partial #'hello-handler system)}}]
   ["/goodbye" {:get {:handler (partial #'goodbye-handler system)}}]])

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
  [system request]
  (log/info (str (:request-method request) " - " (:uri request)))
  (let [handler (reitit-ring/ring-handler
                 (reitit-ring/router
                  (routes system))
                 #'not-found-handler)]
    (handler request)))