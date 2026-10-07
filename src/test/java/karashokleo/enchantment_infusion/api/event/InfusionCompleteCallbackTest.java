package karashokleo.enchantment_infusion.api.event;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InfusionCompleteCallbackTest
{
    @Test
    void callbacksRemainOrderedAndRepeatable()
    {
        InfusionCompleteCallback.Event event = new InfusionCompleteCallback.Event();
        List<String> calls = new ArrayList<>();
        event.register((world, pos, output, inventory, recipe) -> calls.add("first"));
        event.register((world, pos, output, inventory, recipe) -> calls.add("second"));
        event.invoker().onInfusionComplete(null, null, null, null, null);
        event.invoker().onInfusionComplete(null, null, null, null, null);
        assertEquals(List.of("first", "second", "first", "second"), calls);
    }
}
