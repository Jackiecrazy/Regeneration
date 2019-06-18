package me.suff.regeneration.debugger.util;

import me.suff.regeneration.RegenerationMod;
import me.suff.regeneration.util.RegenState.Transition;
import me.suff.regeneration.util.ScheduledAction;
import net.minecraft.entity.player.EntityPlayer;

public class DebuggableScheduledAction extends ScheduledAction {
	public final Transition transition;
	private final EntityPlayer player;
	
	public DebuggableScheduledAction(Transition transition, EntityPlayer player, Runnable callback, long inTicks) {
		super(callback, inTicks);
		this.transition = transition;
		this.player = player;
	}
	
	@Override
	public boolean tick() {
		if (scheduledTick == -1)
			RegenerationMod.LOG.warn(player.getName() + ": Ticking finsished/canceled ScheduledAction (" + transition + ")");

		boolean willExecute = currentTick == scheduledTick;
		
		boolean executed = super.tick();
		if (willExecute != executed)
			throw new IllegalStateException("Execution prospect wasn't true (prospect: " + willExecute + ", result: " + executed + ", cTick: " + currentTick + ", scheduledTick: " + scheduledTick);
		
		return executed;
	}
	
	@Override
	public void cancel() {
		super.cancel();
	}
	
	@Override
	public double getProgress() {
		if (scheduledTick == -1)
			RegenerationMod.LOG.warn(player.getName() + ": Querying progress of canceled/finished transition");
		return super.getProgress();
	}
	
	public Transition getTransition() {
		return transition;
	}
	
}
