package ryoryo.ppa.asm;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModContainer extends DummyModContainer
{
	private static final String VERSION_MAJOR = "GRADLE.VERSION_MAJOR";
	private static final String VERSION_MINOR = "GRADLE.VERSION_MINOR";
	private static final String VERSION_PATCH = "GRADLE.VERSION_PATCH";

	public ModContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "ppa";
		meta.name = "PlacePumpkinAnywhere";
		meta.version = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_PATCH;
		meta.credits = "Many Minecraft Modders!";
		meta.authorList = Arrays.asList("ryoryo");
		meta.description = "This mod allows you to place Pumpkin anywhere.";
		meta.url = "";
		meta.updateJSON = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		return true;
	}

	@Subscribe
	public void modConstruction(FMLConstructionEvent event)
	{
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent event)
	{
	}

	@Subscribe
	public void init(FMLInitializationEvent event)
	{
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event)
	{
	}
}