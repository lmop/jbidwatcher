package com.jbidwatcher.ui;

/*
 * Copyright (c) 2000-2007, CyberFOX Software, Inc. All Rights Reserved.
 *
 * Developed by mrs (Morgan Schweers)
 */

import com.jbidwatcher.util.queue.DropQObject;
import com.jbidwatcher.util.queue.MessageQueue;
import com.jbidwatcher.util.queue.MQFactory;
import com.jbidwatcher.util.config.JConfig;
import com.jbidwatcher.auction.server.AuctionServerManager;
import com.jbidwatcher.auction.AuctionEntry;
import com.jbidwatcher.auction.DeletedEntry;
import com.jbidwatcher.auction.server.AuctionServer;

public class JBWDropHandler implements MessageQueue.Listener {
  private static JBWDropHandler sInstance = null;
  private static boolean do_uber_debug = false;
  private static String lastSeen = null;

  public void messageAction(Object deQ) {
    DropQObject dObj;
    if (deQ instanceof String) {
      dObj = new DropQObject((String)deQ, null, false);
    } else if (deQ instanceof AuctionEntry) {
      AuctionEntry ae = (AuctionEntry) deQ;
      boolean lostAuction = ae.getAuction() == null;
      ae.update();
      if(lostAuction) {
        AuctionsManager.getInstance().addEntry(ae);
      }
      return;
    } else {
      dObj = (DropQObject) deQ;
    }
    String auctionURL = (String)dObj.getData();
    String label = dObj.getLabel();

    if(do_uber_debug) {
      JConfig.log().logDebug("Dropping (action): " + auctionURL);
    }

    String aucId;

    //  Check to see if it's got a protocol ({protocol}:{path})
    //  If not, treat it as an item number alone, in the space of the default auction server.
    if(auctionURL.indexOf(":") != -1) {
      AuctionServer aucServ = AuctionServerManager.getInstance().getServer();
      aucId = aucServ.extractIdentifierFromURLString(auctionURL);
    } else {
      aucId = auctionURL;
    }
    if(dObj.isInteractive()) DeletedEntry.remove(aucId);

    //  We get the identifier from the URL (which is multi-country),
    //  then create an auction entry from the id.
    AuctionEntry aeNew = AuctionEntry.construct(aucId);
    if(aeNew != null && aeNew.isLoaded()) {
      if(label != null) aeNew.setCategory(label);
      aeNew.clearNeedsUpdate();
      JConfig.log().logDebug("Loaded " + aeNew.getIdentifier() + '.');
      lastSeen = aeNew.getIdentifier();
      AuctionsManager.getInstance().addEntry(aeNew);
    } else {
      if(lastSeen == null || !aucId.equals(lastSeen)) {
        JConfig.log().logDebug("Not loaded (" + aucId + ").");
        lastSeen = aucId;
      }
      if(aeNew != null) aeNew.delete();
    }
  }

  public static void start() {
    if(sInstance == null) MQFactory.getConcrete("drop").registerListener(sInstance = new JBWDropHandler());
  }
}
