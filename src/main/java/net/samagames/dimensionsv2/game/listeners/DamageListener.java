package net.samagames.dimensionsv2.game.listeners;
import net.samagames.dimensionsv2.Dimensions;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.utils.ItemUtils;
import org.bukkit.EntityEffect;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tigger_San on 23/04/2017.
 */
public class DamageListener implements Listener
{

    @EventHandler
    public void onDamage(EntityDamageEvent e)
    {

        DimensionsGame game = Dimensions.getInstance().getGame();
        if(game.isNonGameStep()){
            e.setCancelled(true);
            return;
        }

        //Damages
        if (e.getCause() == EntityDamageEvent.DamageCause.MAGIC && game.isNonPVPActive()){
            e.setCancelled(true);
        }
        else if (e.getCause() == EntityDamageEvent.DamageCause.POISON && game.isNonPVPActive()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof ItemFrame){
            e.setCancelled(true);
            return;
        }
        DimensionsGame game = Dimensions.getInstance().getGame();
        if(e.getDamager() instanceof Player && !(e.getEntity() instanceof Player)){
            if(game.isNonGameStep() ){
                e.setCancelled(true);
                return;
            }
            else if(e.getEntity() instanceof ArmorStand){
                e.setCancelled(true);
                e.getEntity().playEffect(EntityEffect.DEATH);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        e.getEntity().remove();
                    }
                }.runTaskLater(Dimensions.getInstance(),20L);
            }
        }
        else if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            if(game.isNonPVPActive()){
                e.setCancelled(true);
            }
            else{
                Player damager = (Player)e.getDamager();
                Player player = (Player) e.getEntity();
                game.playerDamageByPlayer(player,damager);
            }
        }
        else if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
        {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
            {
                if(game.isNonPVPActive()){
                    e.setCancelled(true);
                    return ;
                }
                Player shooter = (Player)((Projectile) e.getDamager()).getShooter();
                Player player = (Player) e.getEntity();
                game.playerDamageByPlayer(player,shooter);
            }
        }

    }



    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player p = event.getEntity();
        DimensionsGame game = Dimensions.getInstance().getGame();
        final List<ItemStack> remove = event.getDrops().stream().filter(stack -> stack.equals(ItemUtils.getTargetItem())|| stack.equals(ItemUtils.getSwapItem())).collect(Collectors.toList());
        for (ItemStack rem : remove){
            event.getDrops().remove(rem);
        }
        event.setDeathMessage("");
        game.die(p);
        game.stumpPlayer(p,false);

    }

    @EventHandler
    public void onStorm(WeatherChangeEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onFire(BlockIgniteEvent e){
        e.setCancelled(true);
    }
}
