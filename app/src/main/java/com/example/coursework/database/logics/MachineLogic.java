package com.example.coursework.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.coursework.database.DatabaseHelper;
import com.example.coursework.database.models.MachineWorkersModel;
import com.example.coursework.database.models.MachineModel;

import java.util.ArrayList;
import java.util.List;

public class MachineLogic {
    Context context;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "machine";
    final String COLUMN_ID = "id";
    final String COLUMN_MACHINE_TYPE = "machine_type";
    final String COLUMN_SHIFT_BEGIN_TIME = "shift_begin_time";
    final String COLUMN_SHIFT_END_TIME = "shift_end_time";
    final String COLUMN_SHIFT_ID = "shift_id";
    final String COLUMN_SHIFT_NAME = "shift_name";

    public MachineLogic(Context context) {
        sqlHelper = new DatabaseHelper(context);
        db = sqlHelper.getWritableDatabase();
        this.context = context;
    }

    public MachineLogic open() {
        db = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public List<MachineModel> getFullList() {
        Cursor cursor = db.rawQuery("select * from " + TABLE, null);
        List<MachineModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            MachineModel obj = new MachineModel();
            int id = cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID));
            obj.setId(id);
            obj.setMachine_type(cursor.getString((int) cursor.getColumnIndex(COLUMN_MACHINE_TYPE)));
            obj.setShift_begin_time(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_BEGIN_TIME)));
            obj.setShift_end_time(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_END_TIME)));
            obj.setShiftId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_SHIFT_ID)));
            obj.setShiftName(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_NAME)));

            MachineWorkersLogic machineWorkersLogic = new MachineWorkersLogic(context);
            machineWorkersLogic.open();
            obj.setMachineWorkers(machineWorkersLogic.getFilteredList(id));
            machineWorkersLogic.close();
            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<MachineModel> getFilteredList(long dateFrom, long dateTo) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_SHIFT_END_TIME + " > " + dateFrom + " and " + COLUMN_SHIFT_END_TIME + " < " + dateTo, null);
        List<MachineModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            MachineModel obj = new MachineModel();
            int id = cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID));
            obj.setId(id);
            obj.setMachine_type(cursor.getString((int) cursor.getColumnIndex(COLUMN_MACHINE_TYPE)));
            obj.setShift_begin_time(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_BEGIN_TIME)));
            obj.setShift_end_time(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_END_TIME)));
            obj.setShiftId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_SHIFT_ID)));
            obj.setShiftName(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_NAME)));

            MachineWorkersLogic machineWorkersLogic = new MachineWorkersLogic(context);
            machineWorkersLogic.open();
            obj.setMachineWorkers(machineWorkersLogic.getFilteredList(id));
            machineWorkersLogic.close();
            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public MachineModel getElement(int id) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_ID + " = " + id, null);
        MachineModel obj = new MachineModel();
        if (!cursor.moveToFirst()) {
            return null;
        }

        obj.setId(id);
        obj.setMachine_type(cursor.getString((int) cursor.getColumnIndex(COLUMN_MACHINE_TYPE)));
        obj.setShift_begin_time(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_BEGIN_TIME)));
        obj.setShift_end_time(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_END_TIME)));
        obj.setShiftId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_SHIFT_ID)));
        obj.setShiftName(cursor.getString((int) cursor.getColumnIndex(COLUMN_SHIFT_NAME)));

        MachineWorkersLogic machineWorkersLogic = new MachineWorkersLogic(context);
        machineWorkersLogic.open();
        obj.setMachineWorkers(machineWorkersLogic.getFilteredList(id));
        machineWorkersLogic.close();

        return obj;
    }

    public void insert(MachineModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_MACHINE_TYPE, model.getMachine_type());
        content.put(COLUMN_SHIFT_BEGIN_TIME, model.getShift_begin_time());
        content.put(COLUMN_SHIFT_END_TIME, model.getShift_end_time());
        content.put(COLUMN_SHIFT_ID, model.getShiftId());
        content.put(COLUMN_SHIFT_NAME, model.getShiftName());

        db.insert(TABLE, null, content);

        MachineWorkersLogic machineWorkersLogic = new MachineWorkersLogic(context);
        machineWorkersLogic.open();
        for (MachineWorkersModel machineWorkersModel : model.getMachineWorkers()) {
            machineWorkersLogic.insert(machineWorkersModel);
        }
        machineWorkersLogic.close();
    }

    public void update(MachineModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_MACHINE_TYPE, model.getMachine_type());
        content.put(COLUMN_SHIFT_BEGIN_TIME, model.getShift_begin_time());
        content.put(COLUMN_SHIFT_END_TIME, model.getShift_end_time());
        content.put(COLUMN_SHIFT_ID, model.getShiftId());
        content.put(COLUMN_SHIFT_NAME, model.getShiftName());
        String where = COLUMN_ID + " = " + model.getId();

        db.update(TABLE, content, where, null);

        MachineWorkersLogic machineWorkersLogic = new MachineWorkersLogic(context);
        machineWorkersLogic.open();
        machineWorkersLogic.deleteByMachineId(model.getId());
        for (MachineWorkersModel machineWorkersModel : model.getMachineWorkers()) {
            machineWorkersLogic.insert(machineWorkersModel);
        }
        machineWorkersLogic.close();
    }

    public void delete(int id) {
        String where = COLUMN_ID + " = " + id;
        db.delete(TABLE, where, null);
        MachineWorkersLogic machineWorkersLogic = new MachineWorkersLogic(context);
        machineWorkersLogic.deleteByMachineId(id);
    }

    public void deleteByShiftId(int shiftId) {
        String where = COLUMN_SHIFT_ID + " = " + shiftId;
        db.delete(TABLE, where, null);
    }
}
