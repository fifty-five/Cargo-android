package com.fiftyfive.cargo.handlers;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.ad4screen.sdk.A4S;
import com.ad4screen.sdk.analytics.Cart;
import com.ad4screen.sdk.analytics.Item;
import com.ad4screen.sdk.analytics.Lead;
import com.ad4screen.sdk.analytics.Purchase;
import com.fiftyfive.cargo.Cargo;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Created by Julien on 02/11/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({A4S.class, Lead.class, Cart.class, Item.class, Purchase.class})

public class AccengageHandlerTest extends TestCase {

    A4S accMock = Mockito.mock(A4S.class);
    Lead leadMock = Mockito.mock(Lead.class);
    Cart cartMock = Mockito.mock(Cart.class);
    Item itemMock = Mockito.mock(Item.class);
    Purchase purchaseMock = Mockito.mock(Purchase.class);

    AccengageHandler handler;
    @Mock Application context;
    @Mock Cargo cargo;


    public void setUp() throws Exception {
        initMocks(this);
        handler = new AccengageHandler();
        handler.cargo = cargo;
        handler.tracker = accMock;
    }

    public void testInitCorrect() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("privateKey", "myPrivateKey");
        map.put("partnerId", "myPartnerId");

        handler.execute("ACC_init", map);

        assertEquals(true, handler.isInitialized());
    }

    public void testInitFail1() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("privateKey", "myPrivateKey");

        handler.execute("ACC_init", map);

        assertEquals(false, handler.isInitialized());
    }

    public void testInitFail2() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("partnerId", "myPartnerId");

        handler.execute("ACC_init", map);

        assertEquals(false, handler.isInitialized());
    }

    public void testExecuteProtection() {
        HashMap<String, Object> map = new HashMap<>();
        Intent myIntent = new Intent();
        map.put("intent", myIntent);

        // the "init" attribute is still set to false
        handler.execute("ACC_intent", map);

        // since the execute method check for the value of "init", it won't call our method
        verify(accMock, times(0)).setIntent(myIntent);
    }

    public void testSetIntent() {
        HashMap<String, Object> map = new HashMap<>();
        Intent myIntent = new Intent();
        map.put("intent", myIntent);

        handler.setInitialize(true);
        handler.execute("ACC_intent", map);

        verify(accMock, times(1)).setIntent(myIntent);
    }


    public void testTagEventFailEventId() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventId", 10);
        map.put("eventName", "myEvent");

        handler.setInitialize(true);
        handler.execute("ACC_tagEvent", map);

        verify(accMock, times(0)).trackEvent(anyLong(), anyString());
    }

    public void testTagEventFailMissingEventName() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventId", 1005);

        handler.setInitialize(true);
        handler.execute("ACC_tagEvent", map);

        verify(accMock, times(0)).trackEvent(anyLong(), anyString());
    }

    public void testCorrectSimpleTagEvent() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventId", 1005);
        map.put("eventName", "myEvent");

        handler.setInitialize(true);
        handler.execute("ACC_tagEvent", map);

        verify(accMock, times(1)).trackEvent(anyLong(), anyString());
    }

    public void testCorrectComplexTagEvent() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventId", 1005);
        map.put("eventName", "myEvent");
        map.put("param1", "value1");
        map.put("param2", "value2");
        map.put("param3", "value3");

        handler.setInitialize(true);
        handler.execute("ACC_tagEvent", map);

        verify(accMock, times(1)).trackEvent(anyLong(), anyString(),
                anyString(), anyString(), anyString());
    }

    public void testCorrectComplexTagEvent2() {
        HashMap<String, Object> map = new HashMap<>();
        String[] parameters = new String[2];
        parameters[0] = "test1";
        parameters[1] = "test2";

        map.put("eventId", 1005);
        map.put("eventName", "myEvent");
        map.put("param1", "value1");
        map.put("param2", 220f);
        map.put("param3", parameters);

        handler.setInitialize(true);
        handler.execute("ACC_tagEvent", map);

        verify(accMock, times(1)).trackEvent(anyLong(), anyString(),
                anyString(), anyString(), anyString());
    }

    public void testCorrectTagView() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("screenName", "myScreen");

        handler.setInitialize(true);
        handler.execute("ACC_tagView", map);

        verify(accMock, times(1)).setView("myScreen");
    }

    public void testFailedTagView() {
        HashMap<String, Object> map = new HashMap<>();

        handler.setInitialize(true);
        handler.execute("ACC_tagView", map);

        verify(accMock, times(0)).setView(anyString());
    }

    public void testFailedTagLead1() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("leadValue", "value");

        handler.setInitialize(true);
        handler.execute("ACC_tagLead", map);

        verify(accMock, times(0)).trackLead(any(Lead.class));
    }

    public void testCorrectTagLead() {
        try {
            PowerMockito.whenNew(Lead.class).withAnyArguments().thenReturn(leadMock);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, Object> map = new HashMap<>();

        map.put("leadLabel", "label");
        map.put("leadValue", "value");

        handler.setInitialize(true);
        handler.execute("ACC_tagLead", map);

        verify(accMock, times(1)).trackLead(any(Lead.class));
    }

    public void testFailedTagLead2() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("leadLabel", "label");

        handler.setInitialize(true);
        handler.execute("ACC_tagLead", map);

        verify(accMock, times(0)).trackLead(any(Lead.class));
    }

    public void testCorrectAddToCart() {
        try {
            PowerMockito.whenNew(Cart.class).withAnyArguments().thenReturn(cartMock);
            PowerMockito.whenNew(Item.class).withAnyArguments().thenReturn(itemMock);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, Object> map = new HashMap<>();

        AccItem item = new AccItem("111x6", "testItem", "testCat", "USD", 66.6, 1);
        map.put("transactionId", "500one+165");
        map.put("item", item);

        handler.setInitialize(true);
        handler.execute("ACC_tagAddToCart", map);

        verify(accMock, times(1)).trackAddToCart(any(Cart.class));
    }

    public void testFailedAddToCart() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("transactionId", "500one+165");
        map.put("item", null);

        handler.setInitialize(true);
        handler.execute("ACC_tagAddToCart", map);

        verify(accMock, times(0)).trackAddToCart(any(Cart.class));
    }

    public void testFailedAddToCart2() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("transactionId", "testID");

        handler.setInitialize(true);
        handler.execute("ACC_tagAddToCart", map);

        verify(accMock, times(0)).trackAddToCart(any(Cart.class));
    }

    public void testCorrectSimpleTagPurchase() {
        try {
            PowerMockito.whenNew(Purchase.class).withAnyArguments().thenReturn(purchaseMock);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, Object> map = new HashMap<>();

        map.put("transactionId", "testID");
        map.put("transactionCurrencyCode", "USD");
        map.put("transactionTotal", 15.0);

        handler.setInitialize(true);
        handler.execute("ACC_tagPurchase", map);

        verify(accMock, times(1)).trackPurchase(any(Purchase.class));
    }

    public void testFailedSimpleTagPurchase() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("transactionId", "testID");
        map.put("transactionCurrencyCode", "USD");
        map.put("transactionTotal", -0.2);

        handler.setInitialize(true);
        handler.execute("ACC_tagPurchase", map);

        verify(accMock, times(0)).trackPurchase(any(Purchase.class));
    }

    public void testMissingArgSimpleTagPurchase() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("transactionId", "testID");
        map.put("transactionTotal", 99.99);

        handler.setInitialize(true);
        handler.execute("ACC_tagPurchase", map);

        verify(accMock, times(0)).trackPurchase(any(Purchase.class));
    }

    public void testCorrectComplexTagPurchase() {
        HashMap<String, Object> map = new HashMap<>();

        AccItem item1 = new AccItem("itemId1", "item1", "category1", "EUR", 99.99, 1);
        AccItem item2 = new AccItem("itemId2", "item2", "category2", "EUR", 55.42, 2);
        List<AccItem> list = new ArrayList<AccItem>();
        list.add(item1);
        list.add(item2);

        map.put("transactionId", "testID");
        map.put("transactionCurrencyCode", "EUR");
        map.put("transactionTotal", 15.0);
        map.put("transactionProducts", list);

        handler.setInitialize(true);
        handler.execute("ACC_tagPurchase", map);

        verify(accMock, times(1)).trackPurchase(any(Purchase.class));
    }

    public void testFailedComplexBecomesSimpleTagPurchase() {
        HashMap<String, Object> map = new HashMap<>();

        Item item1 = new Item("itemId1", "item1", "category1", "EUR", 99.99, 1);
        Item item2 = new Item("itemId2", "item2", "category2", "EUR", 55.42, 2);
        List<Item> list = new ArrayList<Item>();
        list.add(item1);
        list.add(item2);

        map.put("transactionId", "testID");
        map.put("transactionCurrencyCode", "EUR");
        map.put("transactionTotal", 15.0);
        map.put("transactionProducts", list);

        handler.setInitialize(true);
        handler.execute("ACC_tagPurchase", map);

        verify(accMock, times(1)).trackPurchase(any(Purchase.class));
    }

    public void testCorrectUpdateDeviceInfo() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("deviceInfoKey", "testKey");
        map.put("deviceInfoValue", "value");

        handler.setInitialize(true);
        handler.execute("ACC_updateDeviceInfo", map);

        verify(accMock, times(1)).updateDeviceInfo(any(Bundle.class));
    }

    public void testCorrectUpdateDeviceInfoWithDate() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("deviceInfoKey", "testKey");
        map.put("deviceInfoDate", new Date());

        handler.setInitialize(true);
        handler.execute("ACC_updateDeviceInfo", map);

        verify(accMock, times(1)).updateDeviceInfo(any(Bundle.class));
    }

    public void testFailedUpdateDeviceInfo() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("deviceInfoKey", "testKey");

        handler.setInitialize(true);
        handler.execute("ACC_updateDeviceInfo", map);

        verify(accMock, times(0)).updateDeviceInfo(any(Bundle.class));
    }



    public void tearDown() throws Exception {
    }
}