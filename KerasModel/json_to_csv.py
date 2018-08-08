import csv
import json
from collections import OrderedDict
import pandas as pd
import numpy as np
import math as mt

class DefaultOrderedDict(OrderedDict):
    def __missing__(self, key):
        self[key] = type(self)()
        return self[key]

#Load Data as JSON file
x = open('GooglePixel.json')
x = json.load(x)

#Transform JSON data to array
#Using 2-dimension array by indicate the position of data via time index and data head. Then copy it to new array.
print("Tranfrom JSON to Array")
print("Battery")
battNum = len(x["BatteryStatus"])
battCol = len(x["BatteryStatus"][1])
data = DefaultOrderedDict()
for i in range(0,battNum):
    next = 0
    for j in x["BatteryStatus"][i]:
        if j != 'time':
            data[x["BatteryStatus"][i]['time']][next] = x["BatteryStatus"][i][j]
        else:
            next = next - 1
        next = next + 1
prev = next
print("Traffic")
battNum = len(x["TrafficStatistics"])
battCol = len(x["TrafficStatistics"][1])
for i in range(0,battNum):
    next = prev
    for j in x["TrafficStatistics"][i]:
        if j != 'time':
            data[x["TrafficStatistics"][i]['time']][next] = x["TrafficStatistics"][i][j]
        else:
            next = next - 1
        next = next + 1
prev = next
print("Screen")
battNum = len(x["ScreenStatus"])
battCol = len(x["ScreenStatus"][1])
for i in range(0,battNum):
    next = prev
    for j in x["ScreenStatus"][i]:
        if j != 'time':
            data[x["ScreenStatus"][i]['time']][next] = x["ScreenStatus"][i][j]
        else:
            next = next - 1
        next = next + 1
prev = next
print("WIFI")
battNum = len(x["WiFiStatus"])
battCol = len(x["WiFiStatus"][1])
for i in range(0,battNum):
    next = prev
    for j in x["WiFiStatus"][i]:
        if j != 'time':
            data[x["WiFiStatus"][i]['time']][next] = x["WiFiStatus"][i][j]
        else:
            next = next - 1
        next = next + 1
prev = next
print("Bluetooth")
battNum = len(x["BluetoothStatus"])
battCol = len(x["BluetoothStatus"][1])
for i in range(0,battNum):
    next = prev
    for j in x["BluetoothStatus"][i]:
        if j != 'time':
            data[x["BluetoothStatus"][i]['time']][next] = x["BluetoothStatus"][i][j]
        else:
            next = next - 1
        next = next + 1
prev = next
print("Airplane")
battNum = len(x["AirplaneModeStatus"])
battCol = len(x["AirplaneModeStatus"][1])
for i in range(0,battNum):
    next = prev
    for j in x["AirplaneModeStatus"][i]:
        if j != 'time':
            data[x["AirplaneModeStatus"][i]['time']][next] = x["AirplaneModeStatus"][i][j]
        else:
            next = next - 1
        next = next + 1
prev = next
print("Cellular")
battNum = len(x["CellularStatus"])
battCol = len(x["CellularStatus"][1])
for i in range(0,battNum):
    next = prev
    for j in x["CellularStatus"][i]:
        if j != 'time':
            data[x["CellularStatus"][i]['time']][next] = x["CellularStatus"][i][j]
        else:
            next = next - 1
        next = next + 1


# Save data in csv file with NaN value in some attribute that use row from the previous data.
print("Save in csv file")
f = csv.writer(open("datacsv.csv", "w",newline=""))
f.writerow(["Time", "Id_Battery", "Percentage", "Is_Charging", "Chg_AC", "Chg_USB", "Chg_Wireless", "Id_Traffic", "Moblie_Tx", "Moblie_Rx", "Total_Rx", "Total_Tx", "Screen_Status", "Id_Screen", "Id_WIFI", "WIFI_Status", "Id_Blutooth", "Bluetooth_Status", "Id_AirplaneMode", "Airplane_Mode", "Id_Cellular", "Cellular_State", "Cellular_Type"])
for i in range(0,len(data)):
    temp = list(data.keys())[i]
    temp2 = ""
    for j in range(0,70):
        if(data[temp][j] == DefaultOrderedDict() ):
            data[temp][j] = 'NaN'
    print(temp2)
    f.writerow([temp,data[temp][0],data[temp][1],data[temp][2],data[temp][3],data[temp][4],data[temp][5],data[temp][6],data[temp][7],data[temp][8],data[temp][9],data[temp][10],data[temp][11],data[temp][12],data[temp][13],data[temp][14],data[temp][15],data[temp][16],data[temp][17],data[temp][18],data[temp][19],data[temp][20],data[temp][21]])

# Load Data for create 1 min csv file and NaN of each date
data = pd.read_csv('datacsv.csv')
data['Time'] = pd.to_datetime(data['Time'])

# Create 1 min CSV
print("1 min csv File")
idx =  data['Time'].iloc[0]
idxend = data['Time'].iloc[-1]
i = 0
while data['Time'][i] != idxend:
    tempd = data['Time'][i]
    temps = tempd.second
    if mt.isnan(temps):
        print('hello')
        i = i + 1
        continue
    if temps == 0:
        i = i + 1
        continue
    data['Time'][i] = tempd - pd.Timedelta(seconds=temps)
    #print(data['Time'][i])
    i = i + 1
data = data.sort_values(by='Time')

data.to_csv('datasortcsv.csv',sep=',',index = False)

# Create NaN file by staring index with Time of the first real data
print("NaN File")
idx =  data['Time'].iloc[0].round('min')
idxend = data['Time'].iloc[-1].round('min')
df = pd.DataFrame({'Time':[idx],'Chg_USB':[np.NaN],'Chg_Wireless':[np.NaN],'Is_Charging':[np.NaN],'Chg_AC':[np.NaN],'Percentage':[np.NaN],'Total_Tx':[np.NaN],'Total_Rx':[np.NaN],'Moblie_Tx':[np.NaN],'Moblie_Rx':[np.NaN],'Screen_Status':[np.NaN],'WIFI_Status':[np.NaN],'Bluetooth_Status':[np.NaN],'Airplane_Mode':[np.NaN],'Cellular_Type':[np.NaN],'Cellular_State':[np.NaN]})
df['Time'] = pd.to_datetime(df['Time'])
x = 0
while idx != idxend:
    d = pd.Timestamp(idx.year, idx.month, idx.day, idx.hour, idx.minute,idx.second) + pd.Timedelta(minutes=1)
    idx = d
    temp = pd.DataFrame({'Time': [d],'Chg_USB':[np.NaN],'Chg_Wireless':[np.NaN],'Is_Charging':[np.NaN],'Chg_AC':[np.NaN],'Percentage':[np.NaN],'Total_Tx':[np.NaN],'Total_Rx':[np.NaN],'Moblie_Tx':[np.NaN],'Moblie_Rx':[np.NaN],'Screen_Status':[np.NaN],'WIFI_Status':[np.NaN],'Bluetooth_Status':[np.NaN],'Airplane_Mode':[np.NaN],'Cellular_Type':[np.NaN],'Cellular_State':[np.NaN]})
    df = df.append(temp)

df.to_csv('csvappend.csv',sep=',',index=False)

#Load Data to merge
data = pd.read_csv('datasortcsv.csv')
df = pd.read_csv('csvappend.csv')


#Merge
print("Merging")
result = pd.merge(df,data,how = 'left',on='Time')

#Save Break (Merge)
result.to_csv('mergecsv.csv',sep=',',index=False)

#Fill First Row and Last Row
print("Fill Fisrt and Last Row")
sort = pd.read_csv('mergecsv.csv')
sort = sort.drop(['Chg_USB_x', "Chg_Wireless_x", "Is_Charging_x", "Chg_AC_x", "Percentage_x", "Id_Battery", "Total_Tx_x", "Total_Rx_x", "Moblie_Tx_x", "Moblie_Rx_x", "Id_Traffic", "Id_Screen", "Screen_Status_x", "Id_WIFI", "WIFI_Status_x", "Id_Blutooth", "Bluetooth_Status_x", "Airplane_Mode_x", "Id_AirplaneMode", "Cellular_Type_x", "Id_Cellular", "Cellular_State_x"],axis=1)
x = 0
for column in sort:
    if(x == 0):
        x = x + 1
        continue
    i = 0
    if isinstance(sort[column][i], str):
        continue
    else:
        while mt.isnan(sort[column][i]):
            i = i + 1
            if isinstance(sort[column][i], str):
                break;
    sort[column][0] = sort[column][i]
x = 0
for column in sort:
    if(x==0):
        x=x+1
        continue
    i = -1
    if(isinstance(sort[column].iloc[i],str)):
        continue
    else:
        while mt.isnan(sort[column].iloc[i]):
            i = i - 1
            if(isinstance(sort[column].iloc[i],str)):
                break;
    sort[column].iloc[-1] = sort[column].iloc[i]

#Fill Data (Percentage using Interpolation)
print("Fill all data")
sort['Percentage_y'] = sort['Percentage_y'].interpolate()
sort = sort.fillna(method='ffill')

#Change data inside specific row
print("Change WIFIStatus")
for index,row in sort.iterrows():
    if (sort['WIFI_Status_y'][index] == 4):
        sort['WIFI_Status_y'][index] = True
    else:
        sort['WIFI_Status_y'][index] = False
sort.to_csv('break1.csv',sep=',',index=False)
print("Change BluetoothStatus")
for index, row in sort.iterrows():
    if (sort[' Bluetooth_Status'][index] == 11) or (sort[' Bluetooth_Status'][index] == 12):
        sort[' Bluetooth_Status'][index] = True
    else:
        sort[' Bluetooth_Status'][index] = False
sort.to_csv('break2.csv',sep=',',index=False)
sort = pd.read_csv('break2.csv')
print("Change CellularState")
for index, row in sort.iterrows():
    if (sort['Cellular_State_y'][index] == 4 ) or (sort['Cellular_State_y'][index] == 5):
        sort['Cellular_State_y'][index] = True
    else:
        sort['Cellular_State_y'][index] = False
sort.to_csv('break3.csv',sep=',',index=False)
sort = pd.read_csv('break3.csv')
print("Change CellularType")
for index, row in sort.iterrows():
    if (sort['Cellular_Type_y'][index] == "LTE"):
        sort['Cellular_Type_y'][index] = 1
    elif (sort['Cellular_Type_y'][index] == "NONE"):
        sort['Cellular_Type_y'][index] = 2
    elif (sort['Cellular_Type_y'][index] == "GPRS"):
        sort['Cellular_Type_y'][index] = 3
    elif (sort['Cellular_Type_y'][index] == "HSPA+"):
        sort['Cellular_Type_y'][index] = 4
    elif (sort['Cellular_Type_y'][index] == "UNKNOWN"):
        sort['Cellular_Type_y'][index] = 5
    elif (sort['Cellular_Type_y'][index] == "EDGE"):
        sort['Cellular_Type_y'][index] = 6

#Save Finish Data
sort.to_csv('completeddata.csv',sep=',',index=False)