/*
 * Copyright 2011-2011 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.grammar.parser;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static org.intellij.grammar.psi.BnfTypes.*;
import static org.intellij.grammar.parser.GrammarParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GrammarParser implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("org.intellij.grammar.parser.GrammarParser");

  @NotNull
  public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {
    final int level_ = 0;
    boolean result_;
    if (root_ == BNF_ATTR) {
      result_ = attr(builder_, level_ + 1);
    }
    else if (root_ == BNF_ATTR_PATTERN) {
      result_ = attr_pattern(builder_, level_ + 1);
    }
    else if (root_ == BNF_ATTR_VALUE) {
      result_ = attr_value(builder_, level_ + 1);
    }
    else if (root_ == BNF_ATTRS) {
      result_ = attrs(builder_, level_ + 1);
    }
    else if (root_ == BNF_CHOICE) {
      result_ = choice(builder_, level_ + 1);
    }
    else if (root_ == BNF_EXPRESSION) {
      result_ = expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_EXTERNAL_EXPRESSION) {
      result_ = external_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_LITERAL_EXPRESSION) {
      result_ = literal_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_MODIFIER) {
      result_ = modifier(builder_, level_ + 1);
    }
    else if (root_ == BNF_PAREN_EXPRESSION) {
      result_ = paren_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_PAREN_OPT_EXPRESSION) {
      result_ = paren_opt_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_PREDICATE) {
      result_ = predicate(builder_, level_ + 1);
    }
    else if (root_ == BNF_PREDICATE_SIGN) {
      result_ = predicate_sign(builder_, level_ + 1);
    }
    else if (root_ == BNF_QUANTIFIED) {
      result_ = quantified(builder_, level_ + 1);
    }
    else if (root_ == BNF_QUANTIFIER) {
      result_ = quantifier(builder_, level_ + 1);
    }
    else if (root_ == BNF_REFERENCE_OR_TOKEN) {
      result_ = reference_or_token(builder_, level_ + 1);
    }
    else if (root_ == BNF_RULE) {
      result_ = rule(builder_, level_ + 1);
    }
    else if (root_ == BNF_SEQUENCE) {
      result_ = sequence(builder_, level_ + 1);
    }
    else if (root_ == BNF_STRING_LITERAL_EXPRESSION) {
      result_ = string_literal_expression(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      try {
        result_ = parseGrammar(builder_, level_ + 1, 
          new Parser() { public boolean parse(PsiBuilder builder_) { return grammar_element(builder_, level_ + 1); }});
        while (builder_.getTokenType() != null) {
          builder_.advanceLexer();
        }
      }
      finally {
        marker_.done(root_);
      }
    }
    return builder_.getTreeBuilt();
  }

  private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    TokenSet.create(BNF_PAREN_OPT_EXPRESSION, BNF_QUANTIFIED),
    TokenSet.create(BNF_STRING_LITERAL_EXPRESSION, BNF_LITERAL_EXPRESSION),
    TokenSet.create(BNF_CHOICE, BNF_EXTERNAL_EXPRESSION, BNF_LITERAL_EXPRESSION, BNF_PAREN_EXPRESSION,
      BNF_PREDICATE, BNF_QUANTIFIED, BNF_REFERENCE_OR_TOKEN, BNF_SEQUENCE,
      BNF_STRING_LITERAL_EXPRESSION, BNF_PAREN_OPT_EXPRESSION, BNF_EXPRESSION),
  };
  public static boolean type_extends_(IElementType child_, IElementType parent_) {
    for (TokenSet set : EXTENDS_SETS_) {
      if (set.contains(child_) && set.contains(parent_)) return true;
    }
    return false;
  }

  /* ********************************************************** */
  // '{' !attr_start expression '}'
  static boolean alt_choice_expression(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "alt_choice_expression")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
      result_ = consumeToken(builder_, BNF_LEFT_BRACE);
      result_ = result_ && alt_choice_expression_1(builder_, level_ + 1);
      pinned_ = result_; // pin = 2
      result_ = result_ && expression(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACE);
    }
    finally {
      if (!result_ && !pinned_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    }
    return result_ || pinned_;
  }

  // !attr_start
  private static boolean alt_choice_expression_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "alt_choice_expression_1")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_NOT_);
      result_ = !attr_start(builder_, level_ + 1);
    }
    finally {
      marker_.rollbackTo();
      result_ = exitErrorRecordingSection(builder_, result_, level_, false, _SECTION_NOT_, null);
    }
    return result_;
  }


  /* ********************************************************** */
  // attr_start attr_value ';'?
  public static boolean attr(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_);
      result_ = attr_start(builder_, level_ + 1);
      pinned_ = result_; // pin = 1
      result_ = result_ && attr_value(builder_, level_ + 1);
      result_ = result_ && attr_2(builder_, level_ + 1);
    }
    finally {
      if (result_ || pinned_) {
        marker_.done(BNF_ATTR);
      }
      else {
        marker_.rollbackTo();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_RECOVER_, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return attr_recover_until(builder_, level_ + 1); }});
    }
    return result_ || pinned_;
  }

  // ';'?
  private static boolean attr_2(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_2")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      consumeToken(builder_, BNF_SEMICOLON);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // '(' string ')'
  public static boolean attr_pattern(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_pattern")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_LEFT_PAREN);
      result_ = result_ && consumeToken(builder_, BNF_STRING);
      result_ = result_ && consumeToken(builder_, BNF_RIGHT_PAREN);
    }
    finally {
      if (result_) {
        marker_.done(BNF_ATTR_PATTERN);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // !('}'|attr_start)
  static boolean attr_recover_until(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover_until")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_NOT_);
      result_ = !attr_recover_until_0(builder_, level_ + 1);
    }
    finally {
      marker_.rollbackTo();
      result_ = exitErrorRecordingSection(builder_, result_, level_, false, _SECTION_NOT_, null);
    }
    return result_;
  }

  // ('}'|attr_start)
  private static boolean attr_recover_until_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover_until_0")) return false;
    return attr_recover_until_0_0(builder_, level_ + 1);
  }

  // '}'|attr_start
  private static boolean attr_recover_until_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover_until_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_RIGHT_BRACE);
      if (!result_) result_ = attr_start(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // id attr_pattern? '='
  static boolean attr_start(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_ID);
      result_ = result_ && attr_start_1(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, BNF_OP_EQ);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // attr_pattern?
  private static boolean attr_start_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start_1")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      attr_pattern(builder_, level_ + 1);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // (reference_or_token | literal_expression) !'='
  public static boolean attr_value(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = attr_value_0(builder_, level_ + 1);
      result_ = result_ && attr_value_1(builder_, level_ + 1);
    }
    finally {
      if (result_) {
        marker_.done(BNF_ATTR_VALUE);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }

  // (reference_or_token | literal_expression)
  private static boolean attr_value_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_0")) return false;
    return attr_value_0_0(builder_, level_ + 1);
  }

  // reference_or_token | literal_expression
  private static boolean attr_value_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = reference_or_token(builder_, level_ + 1);
      if (!result_) result_ = literal_expression(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // !'='
  private static boolean attr_value_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_1")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_NOT_);
      result_ = !consumeToken(builder_, BNF_OP_EQ);
    }
    finally {
      marker_.rollbackTo();
      result_ = exitErrorRecordingSection(builder_, result_, level_, false, _SECTION_NOT_, null);
    }
    return result_;
  }


  /* ********************************************************** */
  // '{' attr* '}'
  public static boolean attrs(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attrs")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
      result_ = consumeToken(builder_, BNF_LEFT_BRACE);
      pinned_ = result_; // pin = 1
      result_ = result_ && attrs_1(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACE);
    }
    finally {
      if (result_ || pinned_) {
        marker_.done(BNF_ATTRS);
      }
      else {
        marker_.rollbackTo();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    }
    return result_ || pinned_;
  }

  // attr*
  private static boolean attrs_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "attrs_1")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      int offset_ = builder_.getCurrentOffset();
      while (result_ && !builder_.eof()) {
        if (!attr(builder_, level_ + 1)) break;
        if (offset_ == builder_.getCurrentOffset()) {
          builder_.error("Empty element parsed in attrs_1");
          break;
        }
        offset_ = builder_.getCurrentOffset();
      }
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // choice_body
  public static boolean choice(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "choice")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      result_ = choice_body(builder_, level_ + 1);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_CHOICE)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_CHOICE);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // sequence choice_tail*
  static boolean choice_body(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "choice_body")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = sequence(builder_, level_ + 1);
      result_ = result_ && choice_body_1(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // choice_tail*
  private static boolean choice_body_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "choice_body_1")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      int offset_ = builder_.getCurrentOffset();
      while (result_ && !builder_.eof()) {
        if (!choice_tail(builder_, level_ + 1)) break;
        if (offset_ == builder_.getCurrentOffset()) {
          builder_.error("Empty element parsed in choice_body_1");
          break;
        }
        offset_ = builder_.getCurrentOffset();
      }
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // '|' sequence
  static boolean choice_tail(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "choice_tail")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
      result_ = consumeToken(builder_, BNF_OP_OR);
      pinned_ = result_; // pin = 1
      result_ = result_ && sequence(builder_, level_ + 1);
    }
    finally {
      if (!result_ && !pinned_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    }
    return result_ || pinned_;
  }


  /* ********************************************************** */
  // choice?
  public static boolean expression(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_ = true;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      choice(builder_, level_ + 1);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_EXPRESSION)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_EXPRESSION);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // '<<' reference_or_token option * '>>'
  public static boolean external_expression(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "external_expression")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
      result_ = consumeToken(builder_, BNF_EXTERNAL_START);
      result_ = result_ && reference_or_token(builder_, level_ + 1);
      pinned_ = result_; // pin = 2
      result_ = result_ && external_expression_2(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, BNF_EXTERNAL_END);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_EXTERNAL_EXPRESSION)) {
        marker_.drop();
      }
      else if (result_ || pinned_) {
        marker_.done(BNF_EXTERNAL_EXPRESSION);
      }
      else {
        marker_.rollbackTo();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    }
    return result_ || pinned_;
  }

  // option *
  private static boolean external_expression_2(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "external_expression_2")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      int offset_ = builder_.getCurrentOffset();
      while (result_ && !builder_.eof()) {
        if (!option(builder_, level_ + 1)) break;
        if (offset_ == builder_.getCurrentOffset()) {
          builder_.error("Empty element parsed in external_expression_2");
          break;
        }
        offset_ = builder_.getCurrentOffset();
      }
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // attrs | rule
  static boolean grammar_element(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_);
      result_ = attrs(builder_, level_ + 1);
      if (!result_) result_ = rule(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, false, _SECTION_RECOVER_, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return grammar_element_recover(builder_, level_ + 1); }});
    }
    return result_;
  }


  /* ********************************************************** */
  // !('{'|rule_start)
  static boolean grammar_element_recover(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_recover")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_NOT_);
      result_ = !grammar_element_recover_0(builder_, level_ + 1);
    }
    finally {
      marker_.rollbackTo();
      result_ = exitErrorRecordingSection(builder_, result_, level_, false, _SECTION_NOT_, null);
    }
    return result_;
  }

  // ('{'|rule_start)
  private static boolean grammar_element_recover_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_recover_0")) return false;
    return grammar_element_recover_0_0(builder_, level_ + 1);
  }

  // '{'|rule_start
  private static boolean grammar_element_recover_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_recover_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_LEFT_BRACE);
      if (!result_) result_ = rule_start(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // string_literal_expression | number
  public static boolean literal_expression(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expression")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      result_ = string_literal_expression(builder_, level_ + 1);
      if (!result_) result_ = consumeToken(builder_, BNF_NUMBER);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_LITERAL_EXPRESSION)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_LITERAL_EXPRESSION);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // 'private' | 'external' | 'meta'
  public static boolean modifier(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "modifier")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, "private");
      if (!result_) result_ = consumeToken(builder_, "external");
      if (!result_) result_ = consumeToken(builder_, "meta");
    }
    finally {
      if (result_) {
        marker_.done(BNF_MODIFIER);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // quantified | predicate
  static boolean option(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "option")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = quantified(builder_, level_ + 1);
      if (!result_) result_ = predicate(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // simple_paren_expression | alt_choice_expression
  public static boolean paren_expression(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      result_ = simple_paren_expression(builder_, level_ + 1);
      if (!result_) result_ = alt_choice_expression(builder_, level_ + 1);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_PAREN_EXPRESSION)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_PAREN_EXPRESSION);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // '[' expression ']'
  public static boolean paren_opt_expression(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "paren_opt_expression")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
      result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
      pinned_ = result_; // pin = 1
      result_ = result_ && expression(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACKET);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_PAREN_OPT_EXPRESSION)) {
        marker_.drop();
      }
      else if (result_ || pinned_) {
        marker_.done(BNF_PAREN_OPT_EXPRESSION);
      }
      else {
        marker_.rollbackTo();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    }
    return result_ || pinned_;
  }


  /* ********************************************************** */
  // predicate_sign  simple
  public static boolean predicate(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "predicate")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      result_ = predicate_sign(builder_, level_ + 1);
      result_ = result_ && simple(builder_, level_ + 1);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_PREDICATE)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_PREDICATE);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // ('&' | '!')
  public static boolean predicate_sign(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "predicate_sign")) return false;
    return predicate_sign_0(builder_, level_ + 1);
  }

  // '&' | '!'
  private static boolean predicate_sign_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "predicate_sign_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_OP_AND);
      if (!result_) result_ = consumeToken(builder_, BNF_OP_NOT);
    }
    finally {
      if (result_) {
        marker_.done(BNF_PREDICATE_SIGN);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // paren_opt_expression | simple quantifier?
  public static boolean quantified(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "quantified")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      result_ = paren_opt_expression(builder_, level_ + 1);
      if (!result_) result_ = quantified_1(builder_, level_ + 1);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_QUANTIFIED)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_QUANTIFIED);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }

  // simple quantifier?
  private static boolean quantified_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "quantified_1")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = simple(builder_, level_ + 1);
      result_ = result_ && quantified_1_1(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // quantifier?
  private static boolean quantified_1_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "quantified_1_1")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      quantifier(builder_, level_ + 1);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // '?' | '+' | '*'
  public static boolean quantifier(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "quantifier")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_OP_OPT);
      if (!result_) result_ = consumeToken(builder_, BNF_OP_ONEMORE);
      if (!result_) result_ = consumeToken(builder_, BNF_OP_ZEROMORE);
    }
    finally {
      if (result_) {
        marker_.done(BNF_QUANTIFIER);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // id
  public static boolean reference_or_token(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "reference_or_token")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_ID);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_REFERENCE_OR_TOKEN)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_REFERENCE_OR_TOKEN);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // rule_start
  //     expression
  //     attrs? ';'?
  public static boolean rule(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "rule")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
      result_ = rule_start(builder_, level_ + 1);
      pinned_ = result_; // pin = 1
      result_ = result_ && expression(builder_, level_ + 1);
      result_ = result_ && rule_2(builder_, level_ + 1);
      result_ = result_ && rule_3(builder_, level_ + 1);
    }
    finally {
      if (result_ || pinned_) {
        marker_.done(BNF_RULE);
      }
      else {
        marker_.rollbackTo();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    }
    return result_ || pinned_;
  }

  // attrs?
  private static boolean rule_2(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "rule_2")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      attrs(builder_, level_ + 1);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }

  // ';'?
  private static boolean rule_3(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "rule_3")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      consumeToken(builder_, BNF_SEMICOLON);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // modifier* id '::='
  static boolean rule_start(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "rule_start")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = rule_start_0(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, BNF_ID);
      result_ = result_ && consumeToken(builder_, BNF_OP_IS);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // modifier*
  private static boolean rule_start_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "rule_start_0")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      int offset_ = builder_.getCurrentOffset();
      while (result_ && !builder_.eof()) {
        if (!modifier(builder_, level_ + 1)) break;
        if (offset_ == builder_.getCurrentOffset()) {
          builder_.error("Empty element parsed in rule_start_0");
          break;
        }
        offset_ = builder_.getCurrentOffset();
      }
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // option +
  public static boolean sequence(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "sequence")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_);
      result_ = option(builder_, level_ + 1);
      int offset_ = builder_.getCurrentOffset();
      while (result_ && !builder_.eof()) {
        if (!option(builder_, level_ + 1)) break;
        if (offset_ == builder_.getCurrentOffset()) {
          builder_.error("Empty element parsed in sequence");
          break;
        }
        offset_ = builder_.getCurrentOffset();
      }
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_SEQUENCE)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_SEQUENCE);
      }
      else {
        marker_.rollbackTo();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, false, _SECTION_RECOVER_, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return sequence_recover(builder_, level_ + 1); }});
    }
    return result_;
  }


  /* ********************************************************** */
  // !(';'|'|'|'('|')'|'['|']'|'{'|'}') grammar_element_recover
  static boolean sequence_recover(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = sequence_recover_0(builder_, level_ + 1);
      result_ = result_ && grammar_element_recover(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // !(';'|'|'|'('|')'|'['|']'|'{'|'}')
  private static boolean sequence_recover_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_NOT_);
      result_ = !sequence_recover_0_0(builder_, level_ + 1);
    }
    finally {
      marker_.rollbackTo();
      result_ = exitErrorRecordingSection(builder_, result_, level_, false, _SECTION_NOT_, null);
    }
    return result_;
  }

  // (';'|'|'|'('|')'|'['|']'|'{'|'}')
  private static boolean sequence_recover_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover_0_0")) return false;
    return sequence_recover_0_0_0(builder_, level_ + 1);
  }

  // ';'|'|'|'('|')'|'['|']'|'{'|'}'
  private static boolean sequence_recover_0_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover_0_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_SEMICOLON);
      if (!result_) result_ = consumeToken(builder_, BNF_OP_OR);
      if (!result_) result_ = consumeToken(builder_, BNF_LEFT_PAREN);
      if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_PAREN);
      if (!result_) result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
      if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_BRACKET);
      if (!result_) result_ = consumeToken(builder_, BNF_LEFT_BRACE);
      if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_BRACE);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // !(modifier* id '::=' ) reference_or_token | literal_expression | external_expression | paren_expression
  static boolean simple(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = simple_0(builder_, level_ + 1);
      if (!result_) result_ = literal_expression(builder_, level_ + 1);
      if (!result_) result_ = external_expression(builder_, level_ + 1);
      if (!result_) result_ = paren_expression(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // !(modifier* id '::=' ) reference_or_token
  private static boolean simple_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = simple_0_0(builder_, level_ + 1);
      result_ = result_ && reference_or_token(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // !(modifier* id '::=' )
  private static boolean simple_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_NOT_);
      result_ = !simple_0_0_0(builder_, level_ + 1);
    }
    finally {
      marker_.rollbackTo();
      result_ = exitErrorRecordingSection(builder_, result_, level_, false, _SECTION_NOT_, null);
    }
    return result_;
  }

  // (modifier* id '::=' )
  private static boolean simple_0_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0")) return false;
    return simple_0_0_0_0(builder_, level_ + 1);
  }

  // modifier* id '::='
  private static boolean simple_0_0_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = simple_0_0_0_0_0(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, BNF_ID);
      result_ = result_ && consumeToken(builder_, BNF_OP_IS);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // modifier*
  private static boolean simple_0_0_0_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0_0_0")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      int offset_ = builder_.getCurrentOffset();
      while (result_ && !builder_.eof()) {
        if (!modifier(builder_, level_ + 1)) break;
        if (offset_ == builder_.getCurrentOffset()) {
          builder_.error("Empty element parsed in simple_0_0_0_0_0");
          break;
        }
        offset_ = builder_.getCurrentOffset();
      }
    }
    finally {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // '(' expression ')'
  static boolean simple_paren_expression(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple_paren_expression")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final Marker marker_ = builder_.mark();
    try {
      enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
      result_ = consumeToken(builder_, BNF_LEFT_PAREN);
      pinned_ = result_; // pin = 1
      result_ = result_ && expression(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, BNF_RIGHT_PAREN);
    }
    finally {
      if (!result_ && !pinned_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
      result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    }
    return result_ || pinned_;
  }


  /* ********************************************************** */
  // string
  public static boolean string_literal_expression(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "string_literal_expression")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, BNF_STRING);
    }
    finally {
      LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
      if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_STRING_LITERAL_EXPRESSION)) {
        marker_.drop();
      }
      else if (result_) {
        marker_.done(BNF_STRING_LITERAL_EXPRESSION);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


}
