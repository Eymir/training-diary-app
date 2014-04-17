package ru.adhocapp.instaprint.db.entity;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import ru.adhocapp.instaprint.db.DBHelper;

/**
 * Created by Lenovo on 12.04.2014.
 */
public class EntityManager {
    private DBHelper dbHelper;

    public EntityManager(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void persist(Entity entity) {
        if (entity.getId() != null)
            return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            persist(db, entity);
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    public <T> T find(Class<T> entityClass, Long primaryKey) {
        if (primaryKey == null)
            return null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            if (entityClass == Address.class) {
                return (T) dbHelper.getAddressById(db, primaryKey);
            }
            if (entityClass == PurchaseDetails.class) {
                return (T) dbHelper.getPurchaseDetailsById(db, primaryKey);
            }
            if (entityClass == Order.class) {
                Order order = dbHelper.getOrderById(db, primaryKey);
                return (T) order;
            }
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
        return null;
    }

    public void remove(Entity entity) {
        if (entity.getId() == null)
            return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            remove(db, entity);
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    public void merge(Entity entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            merge(db, entity);
        } finally {
            if (db != null && db.isOpen())
                db.close();
        }
    }

    public List findAll(Class entityClass) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            if (entityClass == Address.class) {
                return dbHelper.getAllAddresses(db);
            }
            if (entityClass == PurchaseDetails.class) {
                return dbHelper.getAllPurchaseDetails(db);
            }
            if (entityClass == Order.class) {
                return dbHelper.getAllOrders(db);
            }
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
        return null;
    }


    private void merge(SQLiteDatabase db, Entity entity) {
        if (entity == null) return;
        if (entity.getId() == null) {
            persist(db, entity);
            return;
        }
        if (entity instanceof Address) {
            merge(db, (Address) entity);
        }
        if (entity instanceof PurchaseDetails) {
            merge(db, (PurchaseDetails) entity);
        }
        if (entity instanceof Order) {
            Order order = (Order) entity;
            merge(db, (Entity) order.getPurchaseDetails());
            merge(db, (Entity) order.getAddressFrom());
            merge(db, (Entity) order.getAddressTo());
            merge(db, order);
        }
    }

    private void merge(SQLiteDatabase db, Address address) {
        dbHelper.updateAddress(db, address);
    }

    private void merge(SQLiteDatabase db, PurchaseDetails purchaseDetails) {
        dbHelper.updatePurchaseDetails(db, purchaseDetails);
    }

    private void merge(SQLiteDatabase db, Order order) {
        dbHelper.updateOrder(db, order);
    }

    private void remove(SQLiteDatabase db, Entity entity) {
        if (entity == null) return;
        if (entity instanceof Address) {
            dbHelper.delete(db, entity);
        }
        if (entity instanceof PurchaseDetails) {
            dbHelper.delete(db, entity);
        }
        if (entity instanceof Order) {
            Order order = (Order) entity;
            dbHelper.delete(db, order.getPurchaseDetails());
            remove(db, order);
        }
    }

    private void persist(SQLiteDatabase db, Entity entity) {
        if (entity == null) return;
        if (entity.getId() != null) return;
        if (entity instanceof Address) {
            persist(db, (Address) entity);
        }
        if (entity instanceof PurchaseDetails) {
            persist(db, (PurchaseDetails) entity);
        }
        if (entity instanceof Order) {
            Order order = (Order) entity;
            persist(db, (Entity) order.getPurchaseDetails());
            persist(db, (Entity) order.getAddressFrom());
            persist(db, (Entity) order.getAddressTo());
            persist(db, order);
        }
    }

    private void persist(SQLiteDatabase db, PurchaseDetails purchaseDetails) {
        if (purchaseDetails == null) return;
        purchaseDetails.setId(dbHelper.insertPurchaseDetails(db, purchaseDetails));
    }

    private void persist(SQLiteDatabase db, Order order) {
        if (order == null) return;
        order.setId(dbHelper.insertOrder(db, order));
    }

    private void persist(SQLiteDatabase db, Address address) {
        if (address == null) return;
        address.setId(dbHelper.insertAddress(db, address));
    }
}
