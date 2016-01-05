/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.FrWorks.strategy;

import battlecode.common.RobotController;

/**
 * To make a new Fact, make a new field, then define the isTrue method, which
 * returns true when this fact is true, and false otherwise.
 * @author alexhuleatt
 */
public enum Fact {
    
    BIG_MAP {

        @Override
        public boolean isTrue(RobotController rc) {
            
            
            return false;
        }
        
    },
    SMALL_MAP {

        @Override
        public boolean isTrue(RobotController rc) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    },
    LATE_GAME {

        @Override
        public boolean isTrue(RobotController rc) {
           
            return false;
        }
        
    },
    EARLY_GAME {

        @Override
        public boolean isTrue(RobotController rc) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    },
    SPOOKED_HQ {

        @Override
        public boolean isTrue(RobotController rc) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    },
    TOO_FEW_UNITS {

        @Override
        public boolean isTrue(RobotController rc) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    },
    
    SPOOKED {

        @Override
        public boolean isTrue(RobotController rc) {
            return SPOOKED_HQ.isTrue(rc) || TOO_FEW_UNITS.isTrue(rc);
        }
        
    };
    
    
    
    
    public abstract boolean isTrue(RobotController rc);
    
}
