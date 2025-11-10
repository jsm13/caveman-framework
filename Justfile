help:
  just --list

run: 
  clojure -M -m sandbox.main

nrepl: 
  clojure -M:dev -m nrepl.cmdline

format_check:
  clojure -M:format -m cljfmt.main check src dev test

format:
  clojure -M:format -m cljfmt.main fix src dev test

lint:
  clojure -M:lint -m clj-kondo.main --lint .

test:
  clojure -M:dev -m kaocha.runner