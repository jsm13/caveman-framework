(ns example.math-test
  (:require [clojure.test :as t]))

(t/deftest one-plus-one
  (t/is (= (+ 1 1 ) 2) "one plus one is two!"))