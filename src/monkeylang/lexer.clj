(ns monkeylang.lexer
  (:require [clojure.string :as str]
            [clojure.core.match :refer [match]]))

(match [3 3]
       [0 0] "0 and 0"
       [3 _] "3 and any")

(match "wut"
       [\w \u \t] "w and rest"
       "wut" "got wut")


(def keywords
  {"let" :let
   "fn" :fn
   "true" :true
   "false" :false
   "if" :if
   "else" :else
   "return" :return})

(defn- next-word-token
  "Converts the next word in the string to a token, and the rest of the string."
  [s]
  (let [[word rest] (split-with #(Character/isLetter %) s)
        word (str/lower-case (apply str word))]
    (if-let [type (get keywords word)]
      [{:type type} rest]
      [{:type :identifier :literal word} rest])))

(defn- next-int-token
  "Converts the next int in the string to a token, and the rest of the string."
  [s]
  (let [[int rest] (split-with #(Character/isDigit %) s)
        int (apply str int)]
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
  [s]
  ;; greedily grab all symbols, then walk that back if we don't find a symbol token
  (loop [[symbol-chars rest] (split-with #(not (or (Character/isLetter %) (Character/isDigit %) (Character/isWhitespace %))) s)]
    (let [symbol (apply str symbol-chars)]
      (if-let [type (get symbols symbol)]
        [{:type type} rest]
        (if (= 1 (count symbol-chars))
          [{:type :illegal :literal symbol} rest]
          (let [symbol-chars' (butlast symbol-chars)
                rest' (conj rest (last symbol-chars))]
            (recur [symbol-chars' rest'])))))))

(defn- next-token
  "Returns the next token in the string, and the rest of the string."
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