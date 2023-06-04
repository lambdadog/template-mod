package sh.pea.testmod

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

val MOD_BUS = KotlinModLoadingContext.get().getKEventBus()
val FORGE_BUS = MinecraftForge.EVENT_BUS!!

@Mod(TestMod.MODID)
object TestMod {
    const val MODID = "testmod"
    private val LOGGER: Logger = LogManager.getLogger()

    init {
        LOGGER.log(Level.INFO, "Test Mod has started!")
    }
}