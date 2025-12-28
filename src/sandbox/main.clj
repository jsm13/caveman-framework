(ns sandbox.main
  (:require [sandbox.system :as system])
  (:gen-class))

(defn -main []
  (system/start-system))

(comment
  (println "hello"))