(ns sandbox.main
  (:require [sandbox.system :as system]))

(defn -main []
  (system/start-system))

(comment
  (println "hello"))