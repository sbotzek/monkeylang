(ns monkeylang.repl
  (:require [monkeylang.lexer :as lexer]))

(defn -main
  []
  (loop []
    (print ">> ")
    (flush)
    (let [line (read-line)]
      (doseq [token (lexer/str->tokens line)]
        (println token))
      (recur))))
