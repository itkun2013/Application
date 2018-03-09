/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\yitiji\\7001\\duoduo\\Application\\app\\src\\main\\aidl\\com\\konsung\\aidl\\IKonsungAidlInterface.aidl
 */
package com.konsung.aidl;
// Declare any non-default types here with import statements

public interface IKonsungAidlInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.konsung.aidl.IKonsungAidlInterface
{
private static final java.lang.String DESCRIPTOR = "com.konsung.aidl.IKonsungAidlInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.konsung.aidl.IKonsungAidlInterface interface,
 * generating a proxy if needed.
 */
public static com.konsung.aidl.IKonsungAidlInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.konsung.aidl.IKonsungAidlInterface))) {
return ((com.konsung.aidl.IKonsungAidlInterface)iin);
}
return new com.konsung.aidl.IKonsungAidlInterface.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_trendValue:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.trendValue(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_saveTrend:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.saveTrend(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getEcgConfig:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getEcgConfig(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getNibpConfig:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getNibpConfig(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getSpo2Config:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getSpo2Config(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getT1:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getT1();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getT2:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getT2();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getTd:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getTd();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getIrtempTrend:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getIrtempTrend();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sendNibpConfig:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.sendNibpConfig(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getParamStatus:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getParamStatus(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getWave:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _result = this.getWave(_arg0);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_savedTrendValue:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.savedTrendValue(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_saveToDb:
{
data.enforceInterface(DESCRIPTOR);
this.saveToDb();
reply.writeNoException();
return true;
}
case TRANSACTION_savedWave:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.savedWave(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setWaveNum:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setWaveNum(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_resetWave:
{
data.enforceInterface(DESCRIPTOR);
this.resetWave();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.konsung.aidl.IKonsungAidlInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int trendValue(int param) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(param);
mRemote.transact(Stub.TRANSACTION_trendValue, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void saveTrend(int param, int value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(param);
_data.writeInt(value);
mRemote.transact(Stub.TRANSACTION_saveTrend, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getEcgConfig(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_getEcgConfig, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getNibpConfig(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_getNibpConfig, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getSpo2Config(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_getSpo2Config, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getT1() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getT1, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getT2() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getT2, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getTd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTd, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getIrtempTrend() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getIrtempTrend, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void sendNibpConfig(int id, int value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
_data.writeInt(value);
mRemote.transact(Stub.TRANSACTION_sendNibpConfig, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getParamStatus(int param) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(param);
mRemote.transact(Stub.TRANSACTION_getParamStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public byte[] getWave(int param) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(param);
mRemote.transact(Stub.TRANSACTION_getWave, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int savedTrendValue(int param) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(param);
mRemote.transact(Stub.TRANSACTION_savedTrendValue, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void saveToDb() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_saveToDb, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// 保存到数据库

@Override public void savedWave(int param, java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(param);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_savedWave, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setWaveNum(int num) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(num);
mRemote.transact(Stub.TRANSACTION_setWaveNum, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//设置导联数

@Override public void resetWave() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resetWave, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_trendValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_saveTrend = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getEcgConfig = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getNibpConfig = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getSpo2Config = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getT1 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getT2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getTd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getIrtempTrend = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_sendNibpConfig = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getParamStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getWave = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_savedTrendValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_saveToDb = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_savedWave = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_setWaveNum = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_resetWave = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
}
public int trendValue(int param) throws android.os.RemoteException;
public void saveTrend(int param, int value) throws android.os.RemoteException;
public int getEcgConfig(int id) throws android.os.RemoteException;
public int getNibpConfig(int id) throws android.os.RemoteException;
public int getSpo2Config(int id) throws android.os.RemoteException;
public int getT1() throws android.os.RemoteException;
public int getT2() throws android.os.RemoteException;
public int getTd() throws android.os.RemoteException;
public int getIrtempTrend() throws android.os.RemoteException;
public void sendNibpConfig(int id, int value) throws android.os.RemoteException;
public int getParamStatus(int param) throws android.os.RemoteException;
public byte[] getWave(int param) throws android.os.RemoteException;
public int savedTrendValue(int param) throws android.os.RemoteException;
public void saveToDb() throws android.os.RemoteException;
// 保存到数据库

public void savedWave(int param, java.lang.String value) throws android.os.RemoteException;
public void setWaveNum(int num) throws android.os.RemoteException;
//设置导联数

public void resetWave() throws android.os.RemoteException;
}
