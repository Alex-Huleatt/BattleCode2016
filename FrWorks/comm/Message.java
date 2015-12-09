/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FrWorks.comm;

/**
 * It is the responsibility of the caller to know what a message means. Just add
 * a fancy name for each of the messages you want to convey. Try to have less
 * than 16777216 message types. (We use the leftmost 8 bits for signing.)
 *
 * @author alexhuleatt
 */
public enum Message {

    UNSIGNED, ERROR, 
}
