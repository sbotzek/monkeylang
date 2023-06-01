(ns monkeylang.lexer-test
  (:require [monkeylang.lexer :refer [str->tokens]]
            [monkeylang.helpers :refer [is-no-diff]]
            [clojure.test :refer :all]))

(deftest lexer-tests
  (testing "List of symbols"
    (is-no-diff [[:assign]
                 [:plus]
                 [:lparen]
                 [:rparen]
                 [:lbrace]
                 [:rbrace]
                 [:comma]
                 [:semicolon]]
                (str->tokens "=+(){},;")))
  (testing "Simple script"
    (is-no-diff [[:let]
                 [:identifier "five"]
                 [:assign]
                 [:int "5"]
                 [:semicolon]

                 [:let]
                 [:identifier "ten"]
                 [:assign]
                 [:int "10"]
                 [:semicolon]

                 [:let]
                 [:identifier "add"]
                 [:assign]
                 [:fn]
                 [:lparen]
                 [:identifier "x"]
                 [:comma]
                 [:identifier "y"]
                 [:rparen]
                 [:lbrace]
                 [:identifier "x"]
                 [:plus]
                 [:identifier "y"]
                 [:semicolon]
                 [:rbrace]
                 [:semicolon]

                 [:let]
                 [:identifier "result"]
                 [:assign]
                 [:identifier "add"]
                 [:lparen]
                 [:identifier "five"]
                 [:comma]
                 [:identifier "ten"]
                 [:rparen]
                 [:semicolon]

                 [:bang]
                 [:minus]
                 [:slash]
                 [:asterisk]
                 [:int "5"]
                 [:semicolon]

                 [:int "5"]
                 [:lt]
                 [:int "10"]
                 [:gt]
                 [:int "5"]
                 [:semicolon]

                 [:if]
                 [:lparen]
                 [:int "5"]
                 [:lt]
                 [:int "10"]
                 [:rparen]
                 [:lbrace]
                 [:return]
                 [:true]
                 [:semicolon]
                 [:rbrace]
                 [:else]
                 [:lbrace]
                 [:return]
                 [:false]
                 [:semicolon]
                 [:rbrace]

                 [:int "10"]
                 [:eq]
                 [:int "10"]
                 [:semicolon]

                 [:int "10"]
                 [:not-eq]
                 [:int "9"]
                 [:semicolon]
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
    (is-no-diff [[:assign]
                 [:illegal "`"]
                 [:assign]]
                (str->tokens "=`="))))
