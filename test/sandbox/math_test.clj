(ns sandbox.math-test
  (:require [clojure.test :as t]
            [sandbox.test-system :as test-system]
            [next.jdbc :as jdbc]))

(t/deftest one-plus-one
  (t/is (= (+ 1 1 ) 2) "one plus one is two!"))

(t/deftest counting-works
  (test-system/with-test-db
    (fn [db]
      (jdbc/execute! db ["INSERT INTO prehistoric.hominid(name) VALUES (?)" "Grunto"])
      (jdbc/execute! db ["INSERT INTO prehistoric.hominid(name) VALUES (?)" "Blingus"])
      (t/is (= 
             (:count (jdbc/execute-one! db ["SELECT COUNT(*) as count FROM prehistoric.hominid"])) 
             2)))))

(comment
  (t/run-tests))