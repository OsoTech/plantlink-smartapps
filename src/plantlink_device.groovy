import groovy.json.JsonBuilder

/**
 *  PlantLink Sensor
 *
 *  Copyright 2014 Oso
 *
 */
metadata {
    definition (name: "PlantLink Sensor", namespace: "OsoTech", author: "Oso") {
        // list of capabilities https://graph.api.smartthings.com/ide/doc/capabilities
        // had to search to figure device type examples for the attribute types
        capability "Sensor"

        command "setStatusIcon"
        command "setPlantFuelLevel"
        command "setBatteryLevel"

        attribute "plantStatus","string"
        attribute "plantFuelLevel","number"
        attribute "linkBatteryLevel","string"


        fingerprint profileId: "0104", deviceId: "0302", inClusters: "0000,0001,0003,0B04,FC08", outClusters: "0003"
    }

    simulator {
        status "zero_moisture": "read attr - raw: C072010B040A0001290000, dni: C072, endpoint: 01, cluster: 0B04, size: 0A, attrId: 0100, encoding: 29, value: 0000"
        status "low_moisture": "read attr - raw: C072010B040A0001290000, dni: C072, endpoint: 01, cluster: 0B04, size: 0A, attrId: 0100, encoding: 29, value: 0bda"
        status "high_moisture": "read attr - raw: C072010B040A0001290000, dni: C072, endpoint: 01, cluster: 0B04, size: 0A, attrId: 0100, encoding: 29, value: 196d"
        status "battery": "read attr - raw: C0720100010A000021340A, dni: C072, endpoint: 01, cluster: 0001, size: 0A, attrId: 0000, encoding: 21, value: 0a34"
        status "version": "read attr - raw: C072010000080100201C, dni: C072, endpoint: 01, cluster: 0000, size: 08, attrId: 0001, encoding: 20, value: 1c"
        status "moisture_catchall": "catchall: 0104 0B04 01 01 0140 00 FE40 00 00 0000 0A 01 0001291300"
        status "battery_catchall": "catchall: 0104 0001 01 01 0140 00 FE40 00 00 0000 0A 01 000021860B"
    }

    tiles {
//        standardTile("plantStatusTile", "device.plantStatus", inactiveLabel: True, decoration: "flat") {
//            state "Not Synced", label:"Not Synced", icon:"st.Home.home13"
//            state "0", label: "Too Dry", icon:"st.Home.home13"
//            state "1", label: "A Little Dry", icon:"st.Home.home13"
//            state "2", label: "Good", icon:"st.Home.home13"
//            state "3", label: "Good", icon:"st.Home.home13"
//            state "4", label: "Too Wet", icon:"st.Home.home13"
//            state "No Soil", label: "Sensor is Too Dry", icon:"st.Home.home13"
//            state "Low Battery", label: "Low Battery", icon:"st.Home.home13"
//            state "Waiting on First Measurement", label: "Calibrating", icon:"st.Home.home13"
//        }
        standardTile("tooDryTile", "device.tooDryFlag", inactiveLabel: True, decoration: "flat"){
            state "Off", label: "", backgroundColor:""
            state "On", label: "Too Dry", backgroundColor:"#bc2323"
        }
        standardTile("moistureGoodTile", "device.goodFlag", inactiveLabel: True, decoration: "flat"){
            state "Off", label: "", backgroundColor:""
            state "On", label: "Good", backgroundColor:"#44b621"
        }
        standardTile("tooWetTile", "device.tooWetFlag", inactiveLabel: True, decoration: "flat"){
            state "Off", label: "", backgroundColor:""
            state "On", label: "Too Wet", backgroundColor:"#bc2323"
        }
        standardTile("plantStatusTile", "device.plantStatus", inactiveLabel: True, decoration: "flat") {
            state "Not Synced", label:"Not Synced", icon:"st.Home.home13"
            state "Needs Water", label: "Too Dry", icon:"st.Home.home13", backgroundColor:"#bc2323"
            state "Dry", label: "Dry", icon:"st.Home.home13", backgroundColor:"#bc2323"
            state "Good", label: "Good", icon:"st.Home.home13", backgroundColor:"#44b621"
            state "Good", label: "Good", icon:"st.Home.home13", backgroundColor:"#44b621"
            state "Too Wet", label: "Too Wet", icon:"st.Home.home13", backgroundColor:"#1e9cbb"
            state "Too Dry", label: "Sensor is Too Dry", icon:"st.Home.home13", backgroundColor:"#bc2323"
            state "Low Battery", label: "Low Battery", icon:"st.Home.home13"
            state "Syncing", label: "Calibrating", icon:"st.Home.home13"
        }
        valueTile("plantStatusTextTile", "device.plantStatus", inactiveLabel: True, decoration: "flat") {
            state "plantStatusTextTile", label:'${currentValue}'
        }

        valueTile("plantFuelLevelTile", "device.plantFuelLevel", width: 2, height: 2) {
            state("plantFuelLevel", label: '${currentValue}% Fuel', unit: "",
                    backgroundColors: [
                            [value: 50, color: "#44b621"],
                            [value: 0, color: "#bc2323"],
                            [value: 10, color: "#d04e00"],
                            [value: 20, color: "#f1d801"],
                            [value: 50, color: "#44b621"],
                            [value: 80, color: "#90d2a7"],
                            [value: 100, color: "#1e9cbb"],
                            [value: 125, color: "#153591"],
                    ]
            )
        }
        valueTile("battery", "device.linkBatteryLevel", inactiveLabel: false, decoration: "flat") {
            state "battery", label:'${currentValue}% battery', unit:""
        }

            main "plantStatusTile"
            details(["tooDryTile", "moistureGoodTile", "tooWetTile", "plantFuelLevelTile", "battery", 'plantStatusTextTile', 'plantStatusTile'])
        }
}

def setStatusIcon(value){
//    Also need to handle status 1-3 and not synced
//    PLANT_STATUS_WATERLOGGED = '4'
//    PLANT_STATUS_WATER_STRESSED = '0'
//    PLANT_STATUS_RECENTLY_WATERED = 'Recently Watered'
//    PLANT_STATUS_NO_SOIL = 'No Soil' -- too dry to sense
//    PLANT_STATUS_LINK_MISSING = 'Link Missing' --ignore this
//    PLANT_STATUS_NO_LINK = 'No Link' -- ignore this
//    PLANT_STATUS_WAITING_ON_FIRST_MEASUREMENT = 'Waiting on First Measurement'
//    PLANT_STATUS_HARDWARE_ERROR = 'Hardware Error' --ignore this
//    PLANT_STATUS_LOW_BATTERY = 'Low Battery'

    def status = ''
    if (value == '0'){
        status = 'Needs Water'
        setDerivedAttribute("tooDryFlag", "On")
        setDerivedAttribute("goodFlag", "Off")
        setDerivedAttribute("tooWetFlag", "Off")
    }
    else if (value == '1'){
        status = 'Dry'
        setDerivedAttribute("tooDryFlag", "On")
        setDerivedAttribute("goodFlag", "Off")
        setDerivedAttribute("tooWetFlag", "Off")
    }
    else if (value == '2'){
        status = 'Good'
        setDerivedAttribute("tooDryFlag", "Off")
        setDerivedAttribute("goodFlag", "On")
        setDerivedAttribute("tooWetFlag", "Off")
    }
    else if (value == '3'){
        status = 'Good'
        setDerivedAttribute("tooDryFlag", "Off")
        setDerivedAttribute("goodFlag", "On")
        setDerivedAttribute("tooWetFlag", "Off")
    }
    else if (value == '4'){
        status = 'Too Wet'
        setDerivedAttribute("tooDryFlag", "Off")
        setDerivedAttribute("goodFlag", "Off")
        setDerivedAttribute("tooWetFlag", "On")
    }
    else if (value == 'No Soil'){
        status = 'Too Dry'
        setPlantFuelLevel(0)
        setDerivedAttribute("tooDryFlag", "On")
        setDerivedAttribute("goodFlag", "Off")
        setDerivedAttribute("tooWetFlag", "Off")
    }
    else if (value == 'Recently Watered'){
        status = 'Watered'
        setPlantFuelLevel(100)
        setDerivedAttribute("tooDryFlag", "Off")
        setDerivedAttribute("goodFlag", "On")
        setDerivedAttribute("tooWetFlag", "Off")
    }
    else if (value == 'Low Battery'){
        status = 'Low Battery'
        setDerivedAttribute("tooDryFlag", "Off")
        setDerivedAttribute("goodFlag", "Off")
        setDerivedAttribute("tooWetFlag", "Off")
    }
    else if (value == 'Waiting on First Measurement'){
        status = 'Syncing'
        setDerivedAttribute("tooDryFlag", "Off")
        setDerivedAttribute("goodFlag", "Off")
        setDerivedAttribute("tooWetFlag", "Off")
    }
    else{
        status = "?"
    }
    sendEvent("name":"plantStatus", "value":status, "description":statusText, displayed: true, isStateChange: true)
}

def setPlantFuelLevel(value){
    setDerivedAttribute("plantFuelLevel", value)
}

def setDerivedAttribute(name, value){
//    sendEvent("name":name, "value":value, "description":statusText, displayed: true, isStateChange: true)
    sendEvent("name":name, "value":value, "description":statusText, displayed: true, isStateChange: true)
}

def setBatteryLevel(value){
//    sendEvent("name":"linkBatteryLevel", "value":value, "description":statusText, displayed: true, isStateChange: true)
    setDerivedAttribute("linkBatteryLevel", value)
}

// parse events into attributes
def parse(String description) {
//    http://docs.smartthings.com/en/latest/device-type-developers-guide/anatomy-of-a-device-type.html#parse-method
//    should always pretend to have 100% battery
//    methods available at https://graph.api.smartthings.com/ide/doc/deviceType
//    documentation of device parts at https://graph.api.smartthings.com/ide/doc/device
//    documentation of event data https://graph.api.smartthings.com/ide/doc/event

//    log.debug "displayname ${device.displayName} label ${device.label} id ${device.id} network id ${device.deviceNetworkId}"

    def description_map = parseDescriptionAsMap(description)
    def event_name = ""
    def measurement_map = [
            type: "link",
            signal: "0x00",
            zigbeedeviceid: device.zigbeeId,
            created: new Date().time /1000 as int
    ]
    if(!description_map.value){
        return
    }
    if (description_map.cluster == "0000"){
        //then this is version and can be ignored
        log.debug "zigbee id ${device.zigbeeId} version ${description_map.value}"
        return
    } else if (description_map.cluster == "0001"){
        // then this is battery
        log.debug "zigbee id ${device.zigbeeId} battery ${description_map.value}"
        event_name = "battery_status"
        measurement_map["battery"] = "0x${description_map.value}"
    } else if (description_map.cluster == "0B04"){
        // then this is battery
        log.debug "zigbee id ${device.zigbeeId}  moisture ${description_map.value}"
        measurement_map["moisture"] = "0x${description_map.value}"
        event_name = "moisture_status"
    } else{
        log.debug "zigbee id ${device.zigbeeId} Parsing '${description}'"
        return
    }
    def json_builder = new JsonBuilder(measurement_map)
    def result = createEvent(name: event_name, value: json_builder.toString())
    return result
}


def parseDescriptionAsMap(description) {
    def zigbee_map = zigbee.parse(description)
    if (zigbee_map){
        def return_map = [cluster: zigbee_map.clusterId]
        log.debug(zigbee_map)
        if (!zigbee_map.text)
        {
            return_map.value = null
            return return_map
        }
	}
    (description - "read attr - ").split(",").inject([:]) { map, param ->
        def nameAndValue = param.split(":")
        map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
    }
}
