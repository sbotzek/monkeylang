(ns monkeylang.lexer
  (:require [clojure.string :as str]))

(def keywords
  {"let" :let
   "fn" :fn
   "true" :true
   "false" :false
   "if" :if
   "else" :else
   "return" :return})

(defn- next-word-token
  "Returns: [next-token rest-of-string]"
  [s]
  (let [[word-chars rest] (split-with #(Character/isLetter %) s)
        word (apply str word-chars)]
    (if-let [type (get keywords word)]
      [{:type type} rest]
      [{:type :identifier :literal word} rest])))

(defn- next-int-token
  "Returns: [next-token rest-of-string]"
  [s]
  (let [[int-chars rest] (split-with #(Character/isDigit %) s)
        int (apply str int-chars)]
    [{:type :int :literal int} rest]))

(def symbols
  {"=" :assign
   "+" :plus
   "-" :minus
   "!" :bang
   "*" :asterisk
   "/" :slash
   "==" :eq
   "!=" :not-eq
   "<" :lt
   ">" :gt
   "(" :lparen
   ")" :rparen
   "," :comma
   ";" :semicolon
   "{" :lbrace
   "}" :rbrace})

(defn- next-symbol-token
  "Returns: [next-token rest-of-string]"
  [s]
  ;; greedily grab all symbols, then walk that back if we don't find a symbol token
  (loop [[symbol-chars rest] (split-with #(not (or (Character/isLetter %) (Character/isDigit %) (Character/isWhitespace %))) s)]
    (let [symbol (apply str symbol-chars)]
      (if-let [type (get symbols symbol)]
        [{:type type} rest]
        (if-let [symbol-chars' (butlast symbol-chars)]
          (recur [symbol-chars' (conj rest (last symbol-chars))])
          [{:type :illegal :literal symbol} rest])))))

(defn- next-token
  "Returns: [next-token rest-of-string]"
  [s]
  (when (seq s)
    (let [[c & rest] s]
      (cond
        (Character/isWhitespace c)
        (recur rest)

        (Character/isLetter c)
        (next-word-token s)

        (Character/isDigit c)
        (next-int-token s)

        :else
        (next-symbol-token s)))))

(defn str->tokens
  "Returns a list of tokens from the given string."
  [s]
  (loop [tokens []
         s s]
    (let [[token rest] (next-token s)]
      (if token
        (recur (conj tokens token) rest)
        tokens))))
