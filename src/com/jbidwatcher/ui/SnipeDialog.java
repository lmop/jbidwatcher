package com.jbidwatcher.ui;

/*
 * Copyright (c) 2000-2007, CyberFOX Software, Inc. All Rights Reserved.
 *
 * Developed by mrs (Morgan Schweers)
 */

import com.jbidwatcher.ui.config.JConfigTab;
import com.jbidwatcher.ui.util.*;
import com.jbidwatcher.util.config.JConfig;

import javax.swing.*;
import java.awt.*;

public class SnipeDialog extends BasicDialog {
  private JTextField quantityField, snipeAmount;
  private JCheckBox subtractShipping;
  private JLabel auctionInfo;
  private JLabel quantityLabel;
  String mInitialValue="";

  public SnipeDialog(String initial) {
    super(null, "Sniping", true, JMouseAdapter.getCurrentGraphicsConfiguration());
    if(initial != null && initial.length() != 0) mInitialValue = initial;
    construct();
  }

  public SnipeDialog() {
    super(null, "Sniping", true, JMouseAdapter.getCurrentGraphicsConfiguration());
    construct();
  }

  private void construct() {
    setBasicContentPane(new JPanel(new SpringLayout()));

    addBehavior();
    setupUI();
    //  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
  }

  protected void onOK() {
    dispose();
  }

  protected void onCancel() {
    setVisible(false);
  }

  public String getQuantity() { if(quantityField.isEnabled()) return quantityField.getText(); else return "1"; }
  public String getAmount() { return snipeAmount.getText().replace(',','.'); }
  public boolean subtractShipping() { return subtractShipping.isSelected(); }

  public void useQuantity(boolean enable) {
    quantityLabel.setEnabled(enable);
    quantityField.setEnabled(enable);
  }

  public void setPrompt(String prompt) {
    auctionInfo.setText(prompt);
    auctionInfo.setVisible(true);
    auctionInfo.invalidate();
    getBasicContentPane().validate();
    validate();
  }

  public void clear() {
    getRootPane().setDefaultButton(getButtonOK());
    quantityField.setText("1");
    snipeAmount.setText(mInitialValue);
  }

  private void setupUI() {
    auctionInfo = new JLabel();
    auctionInfo.setText("Auction Information");
    //  The top section is the auction info.
    getBasicContentPane().add(auctionInfo);

    JPanel inputPane = new JPanel();
    inputPane.setLayout(new BoxLayout(inputPane, BoxLayout.Y_AXIS));

    snipeAmount = new JTextField(10);
    snipeAmount.setText(mInitialValue);

    quantityField = new JTextField(10);
    quantityField.setEnabled(false);
    quantityField.setText("1");

    subtractShipping = new JCheckBox();
    subtractShipping.setSelected(JConfig.queryConfiguration("snipe.subtract_shipping", "false").equals("true"));
    subtractShipping.setText("Auto-subtract shipping and insurance (p/p)");

    JPanel promptPane = new JPanel(new SpringLayout());
    JLabel snipeLabel;
    promptPane.add(snipeLabel = new JLabel("How much do you wish to snipe?", JLabel.TRAILING));
    snipeLabel.setLabelFor(snipeAmount);
    promptPane.add(snipeAmount);
    promptPane.add(quantityLabel = new JLabel("Quantity?", JLabel.TRAILING));
    quantityLabel.setLabelFor(quantityField);
    promptPane.add(quantityField);
    SpringUtilities.makeCompactGrid(promptPane, 2, 2, 6, 6, 6, 3);
    getBasicContentPane().add(promptPane);
    getBasicContentPane().add(subtractShipping);

    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());
    bottomPanel.add(JConfigTab.makeLine(getButtonOK(), getButtonCancel()), BorderLayout.EAST);
    getBasicContentPane().add(bottomPanel);
    SpringUtilities.makeCompactGrid(getBasicContentPane(), 4, 1, 6, 6, 6, 6);
  }
}
