package message.servertoclient;

import junit.framework.Assert;
import message.servertoclient.SMessage;
import message.servertoclient.SMessageImpls;

import org.junit.Test;

public class SMessageImplsTest {
	@Test
    public void testNormalActionText() {
    	SMessage CM = SMessageImpls.deserialize("conv/12/james/text: hello");
    	Assert.assertEquals(CM.toString(), "conv/12/james/text: hello");
}
	@Test
    public void testNormalActionExit() {
    	SMessage CM = SMessageImpls.deserialize("conv/12/james/exit");
    	Assert.assertEquals(CM.toString(), "conv/12/james/exit");
}
	@Test
    public void testNormalActionAdd() {
    	SMessage CM = SMessageImpls.deserialize("conv/12/james/add: john");
    	Assert.assertEquals(CM.toString(), "conv/12/james/add: john");
}
	@Test
    public void testAvailablity() {
    	SMessage CM = SMessageImpls.deserialize("online: james");
    	Assert.assertEquals(CM.toString(), "online: james");
}
	@Test
    public void testAvailablityOff() {
    	SMessage CM = SMessageImpls.deserialize("offline: james");
    	Assert.assertEquals(CM.toString(), "offline: james");
}
	@Test
    public void testHandleClaimed() {
    	SMessage CM = SMessageImpls.deserialize("claimed: james");
    	Assert.assertEquals(CM.toString(), "claimed: james");
}
	@Test
    public void testBadHandle() {
    	SMessage CM = SMessageImpls.deserialize("unavailable: james");
    	Assert.assertEquals(CM.toString(), "unavailable: james");
}
	@Test
    public void testid() {
    	SMessage CM = SMessageImpls.deserialize("id: 12");
    	Assert.assertEquals(CM.toString(), "id: 12");
}
}
