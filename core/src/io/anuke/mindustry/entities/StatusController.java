package io.anuke.mindustry.entities;

import io.anuke.mindustry.content.StatusEffects;
import io.anuke.mindustry.type.StatusEffect;
import io.anuke.ucore.core.Timers;

/**Class for controlling status effects on an entity.*/
public class StatusController {
    private static final TransitionResult globalResult = new TransitionResult();

    private io.anuke.mindustry.type.StatusEffect current = StatusEffects.none;
    private float time;

    public void handleApply(Unit unit, io.anuke.mindustry.type.StatusEffect effect, float intensity){
        if(effect == StatusEffects.none) return; //don't apply empty effects

        float newTime = effect.baseDuration*intensity;

        if(effect == current){
            time = Math.max(time, newTime);
        }else {

            current.getTransition(unit, effect, time, newTime, globalResult);
            time = globalResult.time;

            if (globalResult.result != current) {
                current.onTransition(unit, globalResult.result);
                current = globalResult.result;
            }
        }
    }

    public void clear(){
        current = StatusEffects.none;
        time = 0f;
    }

    public void update(Unit unit){
        time = Math.max(time - Timers.delta(), 0);

        if(time <= 0){
            current = StatusEffects.none;
        }else{
            current.update(unit, time);
        }
    }

    public void set(io.anuke.mindustry.type.StatusEffect current, float time){
        this.current = current;
        this.time = time;
    }

    public io.anuke.mindustry.type.StatusEffect current() {
        return current;
    }

    public float getTime() {
        return time;
    }

    public static class TransitionResult{
        public io.anuke.mindustry.type.StatusEffect result;
        public float time;

        public TransitionResult set(StatusEffect effect, float time){
            this.result = effect;
            this.time = time;
            return this;
        }
    }
}