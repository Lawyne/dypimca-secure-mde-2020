package plug.language.tsm.examples.cta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.BehaviorSoup;
import plug.language.tsm.ast.Channel;

public class ExMain {
	

	List<Behavior<Configuration>> waterTank() {

        Behavior<Configuration> w2s =
                new Behavior<>(
                         "refreshSens",
                        (c) -> c.wtTriggerSensor,
                        (c) -> {
                        	c.wtTriggerSensor = false;
                        	return c;
                        }, 
                        Channel.out("measure")
                        ,true);

        Behavior<Configuration> r2i =
                new Behavior<>(
                         "flowIn",
                        (c) -> c.wtWaterLevel < c.wtMaxWaterLevel,
                        (c) -> {
                        	c.wtWaterLevel+=1;
                        	c.wtTriggerSensor = true;
                        	return c;
                        }, 
                        Channel.in("increase")
                        ,false);

        Behavior<Configuration> i2f =
                new Behavior<>(
                         "overflow",
                        (c) -> c.wtWaterLevel == c.wtMaxWaterLevel,
                        (c) -> {
                        	c.wtOverflow = true;
                        	c.wtTriggerSensor = true;
                        	return c;
                        }, 
                        Channel.in("increase")
                        ,false);

        Behavior<Configuration> r2d =
                new Behavior<>(
                         "flowOut",
                        (c) -> c.wtWaterLevel > 0,
                        (c) -> {
                        	c.wtWaterLevel-=1;
                        	c.wtTriggerSensor = true;
                        	return c;
                        }, 
                        Channel.in("decrease")
                        ,false);

        Behavior<Configuration> d2e =
                new Behavior<>(
                         "underflow",
                        (c) -> c.wtWaterLevel == 0,
                        (c) -> {
                        	c.wtTriggerSensor=true;
                        	return c;
                        }, 
                        Channel.in("decrease")
                        ,false);
        return Arrays.asList(w2s,r2i,i2f,r2d,d2e);
	}
	
	List<Behavior<Configuration>> plc() {

        Behavior<Configuration> upd =
                new Behavior<>(
                         "update",
                        (c) -> true,
                        (c) -> {
                        	c.plcWaterLevel=c.sWaterLevel;
                        	c.plcTriggerDecision=true;
                        	return c;
                        }, 
                        Channel.in("updateLevel")
                        ,false);

        Behavior<Configuration> t2r =
                new Behavior<>(
                         "regular",
                        (c) -> c.plcTriggerDecision
                        	&& c.plcWaterLevel<c.plcUpperThreshold
                        	&& c.plcWaterLevel>c.plcLowerThreshold,
                        (c) -> {
                        	c.plcTriggerDecision = false;
                        	c.plcTriggerRegular = true;
                        	return c;
                        }
                        ,true);

        Behavior<Configuration> t2u =
                new Behavior<>(
                         "highThres",
                        (c) -> c.plcTriggerDecision
                    		&& c.plcWaterLevel>=c.plcUpperThreshold,
                        (c) -> {
                        	c.plcTriggerDecision = false;
                        	c.plcTriggerDangerous = true;
                        	c.plcTriggerIVOff = true;
                        	c.plcTriggerPumpOn = true;
                        	return c;
                        }
                        ,true);

        Behavior<Configuration> t2d =
                new Behavior<>(
                         "lowThres",
                        (c) -> c.plcTriggerDecision
                			&& c.plcWaterLevel<=c.plcLowerThreshold,
                        (c) -> {
                        	c.plcTriggerDecision = false;
                        	c.plcTriggerDangerous = true;
                        	c.plcTriggerIVOn = true;
                        	c.plcTriggerPumpOff = true;
                        	return c;
                        }
                        ,true);

        Behavior<Configuration> p2io =
                new Behavior<>(
                         "valveComOn",
                        (c) -> c.plcTriggerIVOn,
                        (c) -> {
                        	c.plcTriggerIVOn = false;
                        	return c;
                        }, 
                        Channel.out("commandIVOn")
                        ,true);

        Behavior<Configuration> p2po =
                new Behavior<>(
                         "pumpComOn",
                        (c) -> c.plcTriggerPumpOn,
                        (c) -> {
                        	c.plcTriggerPumpOn = false;
                        	return c;
                        }, 
                        Channel.out("commandPumpOn")
                        ,true);
        Behavior<Configuration> p2ic =
                new Behavior<>(
                         "valveComOff",
                        (c) -> c.plcTriggerIVOff,
                        (c) -> {
                        	c.plcTriggerIVOff = false;
                        	return c;
                        }, 
                        Channel.out("commandIVOff")
                        ,true);

        Behavior<Configuration> p2pc =
                new Behavior<>(
                         "pumpComOff",
                        (c) -> c.plcTriggerPumpOff,
                        (c) -> {
                        	c.plcTriggerPumpOff = false;
                        	return c;
                        }, 
                        Channel.out("commandPumpOff")
                        ,true);

        Behavior<Configuration> p2d =
                new Behavior<>(
                         "dangerousSignal",
                        (c) -> c.plcTriggerDangerous,
                        (c) -> {
                        	c.plcTriggerDangerous = false;
                        	return c;
                        }, 
                        Channel.out("dangerousLevel")
                        ,true);

        Behavior<Configuration> p2r =
                new Behavior<>(
                         "regularSignal",
                        (c) -> c.plcTriggerRegular,
                        (c) -> {
                        	c.plcTriggerRegular = false;
                        	return c;
                        }, 
                        Channel.out("regularLevel")
                        ,true);
        return Arrays.asList(upd,t2r,t2u,t2d,p2io,p2po,p2ic,p2pc,p2d,p2r);
	}
	
	List<Behavior<Configuration>> scada() {

        Behavior<Configuration> e2r =
                new Behavior<>(
                         "regularLevel",
                        (c) -> true,
                        (c) -> {
                        	c.sEmergency=false;
                        	return c;
                        }, 
                        Channel.in("regularLevel")
                        ,false);

        Behavior<Configuration> a2c =
                new Behavior<>(
                         "corrupt",
                        (c) -> true,
                        (c) -> {
                        	c.sCorrupted = true;
                        	return c;
                        }, 
                        Channel.in("jamNetwork")
                        ,false);

        Behavior<Configuration> r2e =
                new Behavior<>(
                         "dangerousLevel",
                        (c) -> !c.sEmergency&&!c.sAlert&&!c.sCorrupted,
                        (c) -> {
                        	c.sEmergency=true;
                        	return c;
                        }, 
                        Channel.in("dangerousLevel")
                        ,false);

        Behavior<Configuration> e2a =
                new Behavior<>(
                         "alert",
                        (c) -> c.sEmergency&&!c.sCorrupted,
                        (c) -> {
                        	c.sAlert=true;
                        	return c;
                        }, 
                        Channel.in("dangerousLevel")
                        ,false);

        Behavior<Configuration> a2a =
                new Behavior<>(
                         "alert",
                        (c) -> c.sAlert||c.sCorrupted,
                        (c) -> {
                        	return c;
                        }, 
                        Channel.in("dangerousLevel")
                        ,false);
        return Arrays.asList(e2r,r2e,e2a,a2c,a2a);
	}
	
	List<Behavior<Configuration>> inflowValve() {

        Behavior<Configuration> o2crit =
                new Behavior<>(
                         "flowOut1",
                        (c) -> c.iIsOpen && !c.flagIV,
                        (c) -> {
                        	c.flagIV=true;
                        	return c;
                        }
                        ,true);		
		
        Behavior<Configuration> o2o =
                new Behavior<>(
                         "flowOut2",
                        (c) -> c.iIsOpen && (!c.flagPump || c.turn==c.TURN_IV),
                        (c) -> {
                        	c.flagIV=false;
                        	c.turn=c.TURN_PUMP;
                        	return c;
                        }, 
                        Channel.out("increase")
                        ,false);
        
        Behavior<Configuration> f2fc =
                new Behavior<>(
                         "close",
                        (c) -> c.iIsForced,
                        (c) -> {
                        	return c;
                        }, 
                        Channel.in("commandIVOff")
                        ,false);
        
        Behavior<Configuration> a2f =
                new Behavior<>(
                         "forceOpen",
                        (c) -> true,
                        (c) -> {
                        	c.iIsForced = true;
                        	c.iIsOpen = true;
                        	return c;
                        }, 
                        Channel.in("forceOpen")
                        ,false);
        
        Behavior<Configuration> c2o =
                new Behavior<>(
                         "open",
                        (c) -> true,
                        (c) -> {
                        	c.iIsOpen=true;
                        	return c;
                        }, 
                        Channel.in("commandIVOn")
                        ,false);
        
        Behavior<Configuration> o2c =
                new Behavior<>(
                         "close",
                        (c) -> !c.iIsForced,
                        (c) -> {
                        	c.iIsOpen=false;
                        	c.flagIV=false;
                        	return c;
                        }, 
                        Channel.in("commandIVOff")
                        ,false);
        return Arrays.asList(o2crit,o2o,f2fc,a2f,o2c,c2o);
	}

	List<Behavior<Configuration>> attacker() {

        Behavior<Configuration> z2o =
                new Behavior<>(
                         "jamNetwork",
                        (c) -> !c.aHasJammedNetwork,
                        (c) -> {
                        	c.aHasJammedNetwork = true;
                        	return c;
                        }, 
                        Channel.out("jamNetwork")
                        ,false);
        
        Behavior<Configuration> o2t =
                new Behavior<>(
                         "forceOpen",
                        (c) -> !c.aHasForced,
                        (c) -> {
                        	c.aHasForced = true;
                        	return c;
                        }, 
                        Channel.out("forceOpen")
                        ,false);
        
        Behavior<Configuration> t2t =
                new Behavior<>(
                         "manualInput",
                        (c) -> !c.aHasManuallyInput,
                        (c) -> {
                        	c.aHasManuallyInput = true;
                        	return c;
                        }, 
                        Channel.out("manualInput")
                        ,false);
        
        Behavior<Configuration> s2s =
                new Behavior<>(
                         "corruptSensor",
                        (c) -> !c.aHasCorruptedSensor,
                        (c) -> {
                        	c.aHasCorruptedSensor = true;
                        	return c;
                        }, 
                        Channel.out("corruptSensor")
                        ,false);
        
        return Arrays.asList(z2o,o2t,t2t,s2s);
	}
	
	List<Behavior<Configuration>> manualValve() {

        Behavior<Configuration> o2c =
                new Behavior<>(
                         "close",
                        (c) -> c.mIsOpen,
                        (c) -> {
                        	c.mIsOpen = false;
                        	return c;	
                        }, 
                        Channel.in("manualInput")
                        ,false);

        Behavior<Configuration> c2c =
                new Behavior<>(
                         "flowOut2",
                        (c) -> !c.mIsOpen,
                        (c) -> c, 
                        Channel.in("flow")
                        ,false);
        

        Behavior<Configuration> c2o =
                new Behavior<>(
                         "open",
                        (c) -> !c.mIsOpen,
                        (c) -> {
                        	c.mIsOpen = true;
                        	return c;
                        }, 
                        Channel.in("manualInput")
                        ,false);        

        Behavior<Configuration> o2u =
                new Behavior<>(
                         "flowOut1",
                        (c) -> c.mIsOpen,
                        (c) -> {
                        	c.mTriggerDecrease = true;
                        	return c;
                        }, 
                        Channel.in("flow")
                        ,false);

        Behavior<Configuration> u2o =
                new Behavior<>(
                         "flowIn",
                        (c) -> c.mTriggerDecrease,
                        (c) -> {
                        	c.mTriggerDecrease = false;
                        	return c;
                        }, 
                        Channel.out("decrease")
                        ,true);
        
        return Arrays.asList(o2c,c2c,c2o,o2u,u2o);
	}
	
	List<Behavior<Configuration>> pump() {
		
        Behavior<Configuration> o2crit =
                new Behavior<>(
                         "flowIn1",
                        (c) -> c.pIsOpen && !c.flagPump,
                        (c) -> {
                        	c.flagPump=true;
                        	return c;
                        }
                        ,true);	

        Behavior<Configuration> flow =
                new Behavior<>(
                         "flowIn2",
                        (c) -> c.pIsOpen && (!c.flagIV || c.turn==c.TURN_PUMP),
                        (c) -> {
                        	c.flagPump=false;
                        	c.turn=c.TURN_IV;
                        	return c;
                        },  
                        Channel.out("flow")
                        ,false);        
        
        Behavior<Configuration> o2c =
                new Behavior<>(
                        "switchOff",
                        (c) ->    true,
                        (c) -> {
                            c.pIsOpen = false;
                            c.flagPump=false;
                            return c;
                        },
                        Channel.in("commandPumpOff")
                        );
        
        Behavior<Configuration> c2o =
                new Behavior<>(
                         "Pump.c2o",
                        (c) -> true,
                        (c) -> {
                            c.pIsOpen = true;                            
                            return c;
						}, 
                        Channel.in("commandPumpOn")							 
                        );
        
        return Arrays.asList(o2crit, flow, o2c, c2o);
	}
	
	List<Behavior<Configuration>> sensor() {

        Behavior<Configuration> f2c =
                new Behavior<>(
                         "update",
                        (c) -> true,
                        (c) -> {
                            c.sWaterLevel = c.wtWaterLevel;
                            c.sTriggerSensor = true;
                            return c;
						},  
                        Channel.in("measure")
                        ,false);
        Behavior<Configuration> c2f =
                new Behavior<>(
                         "refreshPLC1",
                         (c) -> c.sTriggerSensor && !c.sIsCorrupted,
                         (c) -> {
                             c.sTriggerSensor = false;                            
                             return c;
 						},   
                        Channel.out("updateLevel"),
                        true);
        Behavior<Configuration> c2fa =
                new Behavior<>(
                         "refreshPLC2",
                         (c) -> c.sTriggerSensor && c.sIsCorrupted,
                         (c) -> {
                             c.sTriggerSensor = false;                            
                             return c;
 						},
                         true);
        Behavior<Configuration> a2a =
                new Behavior<>(
                         "disable",
                        (c) -> true,
                        (c) -> {
                        	c.sIsCorrupted = true;
                        	return c;
                        }, 
                        Channel.in("corruptSensor")
                        ,false);
        return Arrays.asList(f2c,c2f,c2fa,a2a);
	}
	
	 public BehaviorSoup<Configuration> model() {
         BehaviorSoup<Configuration> soup = new BehaviorSoup<>(new Configuration());

         List<Behavior<Configuration>> pump = pump();
         List<Behavior<Configuration>> sensor = sensor();
         List<Behavior<Configuration>> manualValve = manualValve();
         List<Behavior<Configuration>> attacker = attacker();
         List<Behavior<Configuration>> inflowValve = inflowValve();
         List<Behavior<Configuration>> scada = scada();
         List<Behavior<Configuration>> plc = plc();
         List<Behavior<Configuration>> waterTank = waterTank();

         soup.behaviors.addAll(pump);
         soup.behaviors.addAll(sensor);
         soup.behaviors.addAll(manualValve);
         soup.behaviors.addAll(attacker);
         soup.behaviors.addAll(inflowValve);
         soup.behaviors.addAll(scada);
         soup.behaviors.addAll(plc);
         soup.behaviors.addAll(waterTank);
         
         return soup;
     }
}
