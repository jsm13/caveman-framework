(ns sandbox.hello.routes
  (:require [next.jdbc :as jdbc]
            [sandbox.system :as-alias system]
            [sandbox.page-html.core :as page-html]
            [hiccup2.core :as hiccup]))

(defn greet [said-goodbye] 
  (if-not said-goodbye
    "Hello "
    "oh! Hello, again, "))

(defn hello-handler
  [{::system/keys [db]} _request]
  (let [greeting (greet (:said-goodbye true))
        {:keys [planet]} (jdbc/execute-one!
                          db
                          ["SELECT 'earth' as planet"])]
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :session {:foo "bar"}
     :body (str
            (hiccup/html
             (page-html/view :body
                             [:h1 (str greeting planet)])))}))

(defn routes
  [system]
  [["/" {:get {:handler (partial #'hello-handler system)}}]])