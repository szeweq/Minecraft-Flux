package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.MCFluxResources;
import szewek.mcflux.U;
import szewek.mcflux.api.MCFluxAPI;
import szewek.mcflux.api.ex.Battery;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.api.fe.FE;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredContainer;
import szewek.mcflux.api.fe.IFlavorEnergy;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.blocks.BlockSided;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.network.MCFluxNetwork;
import szewek.mcflux.network.msg.MsgUpdateClient;
import szewek.mcflux.network.msg.MsgUpdateServer;
import szewek.mcflux.util.TransferType;

import javax.annotation.Nonnull;
import java.util.function.IntBinaryOperator;

import static szewek.mcflux.config.MCFluxConfig.CHUNK_CHARGER_TRANS;

public class TileEntityEnergyMachine extends TileEntityWCEAware implements ITickable {
	private FlavoredContainer cnt = null;
	private boolean oddTick = true, clientUpdate = true, serverUpdate = false;
	private TransferType[] sideTransfer = new TransferType[]{TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE};
	private long[] sideValues = new long[]{0, 0, 0, 0, 0, 0};
	private IBlockState cachedState = MCFluxResources.SIDED.getDefaultState();
	private IntBinaryOperator module;
	private int moduleId;

	public IBlockState getCachedState() {
		return cachedState;
	}

	public void setModuleId(int id) {
		moduleId = id;
		module = getModule(id);
	}

	public int getModuleId() {
		return moduleId;
	}

	private IntBinaryOperator getModule(int i) {
		switch (i) {
			case 0: return this::moduleEnergyDistributor;
			case 1: return this::moduleChunkCharger;
			case 2: return this::moduleFlavorDistributor;
			case 3: return this::moduleChunkSprayer;
		}
		return null;
	}

	@Override
	public void setPos(@Nonnull BlockPos bp) {
		super.setPos(bp);

	}

	@Override public void onLoad() {
		if (worldObj.isRemote) {
			MCFluxNetwork.toServer(MsgUpdateClient.with(pos));
			clientUpdate = false;
		}
	}

	@Override
	public void update() {
		super.update();
		if (worldObj.isRemote && clientUpdate) {
			MCFluxNetwork.toServer(MsgUpdateClient.with(pos));
			clientUpdate = false;
		} else if (!worldObj.isRemote && serverUpdate) {
			MCFluxNetwork.toDimension(MsgUpdateServer.with(pos, sideTransfer), worldObj.provider.getDimension());
			serverUpdate = false;
		}
		if (!worldObj.isRemote && wce != null && ((moduleId < 2 && bat != null) || cnt != null)) {
			int i = oddTick ? 0 : 3, m = oddTick ? 3 : 6;
			for (int j = i; j < m; j++)
				sideValues[j] = 0;
			if (module != null)
				module.applyAsInt(i, m);
		}
		oddTick = !oddTick;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		moduleId = nbt.getInteger("module");
		int[] sides = nbt.getIntArray("sides");
		if (sides.length != 6) return;
		TransferType[] tt = TransferType.values();
		for (int i = 0; i < 6; i++) {
			sideTransfer[i] = tt[sides[i]];
			cachedState = cachedState.withProperty(BlockSided.sideFromId(i), sides[i]);
		}
		module = getModule(moduleId);
		serverUpdate = true;
	}

	@Override
	@Nonnull
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		int[] sides = new int[6];
		for (int i = 0; i < 6; i++) {
			sides[i] = sideTransfer[i].ord;
		}
		nbt.setIntArray("sides", sides);
		nbt.setInteger("module", moduleId);
		return nbt;
	}

	@Override
	public boolean shouldRefresh(World w, BlockPos pos, @Nonnull IBlockState obs, @Nonnull IBlockState nbs) {
		return obs.getBlock() != nbs.getBlock() || obs.getValue(BlockEnergyMachine.VARIANT) != nbs.getValue(BlockEnergyMachine.VARIANT);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override protected boolean updateVariables() {
		if ((updateMode & 3) != 0 && pos != null) {
			cnt = worldObj != null && !worldObj.isRemote ? wce.getFlavorEnergyChunk(pos.getX(), pos.getY(), pos.getZ()) : null;
		}
		return true;
	}

	public void switchSideTransfer(EnumFacing f) {
		int s = f.getIndex();
		int v = (sideTransfer[s].ord + 1) % 3;
		sideTransfer[s] = TransferType.values()[v];
		cachedState = cachedState.withProperty(BlockSided.sideFromId(s), v);
		MCFluxNetwork.toDimension(MsgUpdateServer.with(pos, sideTransfer), worldObj.provider.getDimension());
		markDirty();
	}

	public long getTransferSide(EnumFacing f) {
		return sideValues[f.getIndex()];
	}

	public TransferType[] getAllTransferSides() {
		return sideTransfer;
	}

	public void updateTransferSides(TransferType[] tts) {
		for (int i = 0; i < 6; i++) {
			sideTransfer[i] = tts[i];
			cachedState = cachedState.withProperty(BlockSided.sideFromId(i), tts[i].ord);
		}
	}

	private int moduleEnergyDistributor(int i, int m) {
		for (; i < m; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			TileEntity te = worldObj.getTileEntity(pos.offset(f, 1));
			if (te == null)
				continue;
			f = f.getOpposite();
			IEnergy ea = MCFluxAPI.getEnergySafely(te, f);
			if (ea == null)
				continue;
			switch (tt) {
				case INPUT:
					sideValues[i] = U.transferEnergy(ea, bat, MCFluxConfig.ENERGY_DIST_TRANS * 2) / 2;
					break;
				case OUTPUT:
					sideValues[i] = U.transferEnergy(bat, ea, MCFluxConfig.ENERGY_DIST_TRANS * 2) / 2;
					break;
			}
		}
		return 0;
	}

	private int moduleChunkCharger(int i, int m) {
		for (; i < m; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			BlockPos bpc = pos.offset(f, 16);
			Battery ebc = wce.getEnergyChunk(bpc.getX(), bpc.getY(), bpc.getZ());
			if (ebc == null)
				continue;
			switch (tt) {
				case INPUT:
					sideValues[i] = U.transferEnergy(ebc, bat, CHUNK_CHARGER_TRANS * 2) / 2;
					break;
				case OUTPUT:
					sideValues[i] = U.transferEnergy(bat, ebc, CHUNK_CHARGER_TRANS * 2) / 2;
					break;
				default:
			}
		}
		return 0;
	}

	private int moduleFlavorDistributor(int i, int m) {
		for (; i < m; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			TileEntity te = worldObj.getTileEntity(pos.offset(f, 1));
			if (te == null)
				continue;
			f = f.getOpposite();
			IFlavorEnergy fea = te.getCapability(FE.CAP_FLAVOR_ENERGY, f);
			if (fea == null)
				continue;
			Flavored[] lfl;
			switch (tt) {
				case INPUT:
					lfl = fea.allFlavorsAcceptable();
					for (Flavored fl : lfl)
						U.transferFlavorEnergy(fea, cnt, fl, CHUNK_CHARGER_TRANS * 2);
					break;
				case OUTPUT:
					lfl = fea.allFlavorsContained();
					for (Flavored fl : lfl)
						U.transferFlavorEnergy(cnt, fea, fl, CHUNK_CHARGER_TRANS * 2);
					break;
				default:
			}
		}
		return 0;
	}

	private int moduleChunkSprayer(int i, int m) {
		for (; i < m; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			BlockPos bpc = pos.offset(f, 16);
			FlavoredContainer efc = wce.getFlavorEnergyChunk(bpc.getX(), bpc.getY(), bpc.getZ());
			if (efc == null)
				continue;
			Flavored[] lfl;
			switch (tt) {
				case INPUT:
					lfl = efc.allFlavorsContained();
					for (Flavored fl : lfl)
						U.transferFlavorEnergy(efc, cnt, fl, CHUNK_CHARGER_TRANS * 2);
					break;
				case OUTPUT:
					lfl = cnt.allFlavorsContained();
					for (Flavored fl : lfl)
						U.transferFlavorEnergy(cnt, efc, fl, CHUNK_CHARGER_TRANS * 2);
					break;
				default:
			}
		}
		return 0;
	}
}
