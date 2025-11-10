(ns sandbox.hello.routes
  (:require [next.jdbc :as jdbc]
            [sandbox.system :as-alias system]
            [hiccup2.core :as hiccup]))

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

(defn routes
  [system]
  [["/" {:get {:handler (partial #'hello-handler system)}}]])