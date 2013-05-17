package message.clienttoserver;

import junit.framework.Assert;
import message.clienttoserver.CMessage;
import message.clienttoserver.CMessageImpls;

import org.junit.Test;

public class CMessageImplsTest {
	@Test
    public void testGetId() {
    	CMessage CM = CMessageImpls.deserialize("getid/santhosh");
    	Assert.assertEquals(CM.toString(), "getid/santhosh");
	}
	
	@Test
    public void testHandle() {
    	CMessage CM = CMessageImpls.deserialize("handle: santhosh");
    	Assert.assertEquals(CM.toString(), "handle: santhosh");
	}
	
	@Test
    public void testGetUsers() {
    	CMessage CM = CMessageImpls.deserialize("getusers/santhosh");
    	Assert.assertEquals(CM.toString(), "getusers/santhosh");
	}
}
