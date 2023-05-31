(ns monkeylang.lexer-test
  (:require [monkeylang.lexer :refer [str->tokens]]
            [clojure.data :as data]
            [clojure.test :refer :all]))

(defn is-no-diff
  [expected result]
  (let [[missing-from-result missing-from-expected in-both] (data/diff expected result)]
    (is (empty? missing-from-result))
    (is (empty? missing-from-expected))))

(deftest lexer-tests
  (testing "List of symbols"
    (is-no-diff [{:type :assign}
                 {:type :plus}
                 {:type :lparen}
                 {:type :rparen}
                 {:type :lbrace}
                 {:type :rbrace}
                 {:type :comma}
                 {:type :semicolon}]
                (str->tokens "=+(){},;")))
  (testing "Simple script"
    (is-no-diff [{:type :let}
                 {:type :identifier :literal "five"}
                 {:type :assign}
                 {:type :int :literal "5"}
                 {:type :semicolon}

                 {:type :let}
                 {:type :identifier :literal "ten"}
                 {:type :assign}
                 {:type :int :literal "10"}
                 {:type :semicolon}

                 {:type :let}
                 {:type :identifier :literal "add"}
                 {:type :assign}
                 {:type :fn}
                 {:type :lparen}
                 {:type :identifier :literal "x"}
                 {:type :comma}
                 {:type :identifier :literal "y"}
                 {:type :rparen}
                 {:type :lbrace}
                 {:type :identifier :literal "x"}
                 {:type :plus}
                 {:type :identifier :literal "y"}
                 {:type :semicolon}
                 {:type :rbrace}
                 {:type :semicolon}

                 {:type :let}
                 {:type :identifier :literal "result"}
                 {:type :assign}
                 {:type :identifier :literal "add"}
                 {:type :lparen}
                 {:type :identifier :literal "five"}
                 {:type :comma}
                 {:type :identifier :literal "ten"}
                 {:type :rparen}
                 {:type :semicolon}

                 {:type :bang}
                 {:type :minus}
                 {:type :slash}
                 {:type :asterisk}
                 {:type :int :literal "5"}
                 {:type :semicolon}

                 {:type :int :literal "5"}
                 {:type :lt}
                 {:type :int :literal "10"}
                 {:type :gt}
                 {:type :int :literal "5"}
                 {:type :semicolon}

                 {:type :if}
                 {:type :lparen}
                 {:type :int :literal "5"}
                 {:type :lt}
                 {:type :int :literal "10"}
                 {:type :rparen}
                 {:type :lbrace}
                 {:type :return}
                 {:type :true}
                 {:type :semicolon}
                 {:type :rbrace}
                 {:type :else}
                 {:type :lbrace}
                 {:type :return}
                 {:type :false}
                 {:type :semicolon}
                 {:type :rbrace}

                 {:type :int :literal "10"}
                 {:type :eq}
                 {:type :int :literal "10"}
                 {:type :semicolon}

                 {:type :int :literal "10"}
                 {:type :not-eq}
                 {:type :int :literal "9"}
                 {:type :semicolon}
                 ]
                (str->tokens
                 "let five = 5;
                  let ten = 10;

                  let add = fn(x, y) {
                    x + y;
                  };

                  let result = add(five, ten);

                  !-/*5;

                  5 < 10 > 5;
                  if (5 < 10) {
                    return true;
                  } else {
                    return false;
                  }

                  10 == 10;
                  10 != 9;")))
  (testing "Illegal symbol"
    (is-no-diff [{:type :assign}
                 {:type :illegal :literal "`"}
                 {:type :assign}]
                (str->tokens "=`="))))
