package com.fiftyfive.cargo.handlers;

import android.app.Application;

import com.atinternet.tracker.Tracker;
import com.fiftyfive.cargo.Cargo;
import com.fiftyfive.cargo.models.Transaction;


import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Created by dali on 10/12/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(Tracker.class)

public class ATInternetHandlerTest extends TestCase {

    Tracker atTrackerMock = mock(Tracker.class);
    ATInternetHandler handler;
    @Mock Application context;
    @Mock Cargo cargo;


    public void setUp() throws Exception {
        initMocks(this);
        handler = new ATInternetHandler();
        PowerMockito.mockStatic(Tracker.class);
        handler.atTracker = atTrackerMock;
        handler.cargo = cargo;
    }

    public void testTagScreen(){
        HashMap<String, Object> map= new HashMap<>();
        map.put("screenName", "Home");
        handler.execute("AT_tagScreen", map);

        verify(atTrackerMock, times(1));

    }

    public void testTagTransaction(){
        HashMap<String, Object> item= new HashMap<>();
        item.put(Transaction.TRANSACTION_PRODUCT_NAME, "TEST");
        item.put(Transaction.TRANSACTION_PRODUCT_SKU, "123test");
        item.put(Transaction.TRANSACTION_PRODUCT_CATEGORY, "catTest");
        item.put(Transaction.TRANSACTION_PRODUCT_PRICE, "1000");
        item.put(Transaction.TRANSACTION_PRODUCT_QUANTITY, "5");

        HashMap<String, Object> param= new HashMap<>();
        param.put(Transaction.TRANSACTION_PRODUCTS, item);
        param.put(Transaction.TRANSACTION_ID, "transactionTest");
        param.put(Transaction.TRANSACTION_TOTAL, 5000);
        param.put("idCart", "lepanier");

        handler.execute("AT_tagTransaction", param);

        verify(atTrackerMock, times(1));
    }

    public void tearDown() throws Exception {

    }
}