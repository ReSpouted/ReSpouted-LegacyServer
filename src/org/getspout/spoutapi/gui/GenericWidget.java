package org.getspout.spoutapi.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.packet.PacketUtil;

public abstract class GenericWidget implements Widget{
	protected int X = 0;
	protected int Y = 0;
	protected int offsetX = 0;
	protected int offsetY = 0;
	protected int width = 0;
	protected int height = 0;
	protected boolean visible = true;
	protected transient boolean dirty = true;
	protected transient Screen screen = null;
	protected RenderPriority priority = RenderPriority.Normal;
	protected UUID id = UUID.randomUUID();
	protected String tooltip = "";
	protected Container container = null;
	protected transient Plugin plugin = null;
	protected WidgetAnchor anchor = WidgetAnchor.TOP_LEFT;
	
	public GenericWidget() {

	}
	
	@Override
	public int getNumBytes() {
		return 38 + PacketUtil.getNumBytes(tooltip);
	}

	@Override
	public int getVersion() {
		return 2;
	}
	
	public GenericWidget(int X, int Y, int width, int height) {
		this.X = X;
		this.Y = Y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Widget setAnchor(WidgetAnchor anchor) {
		this.anchor = anchor;
		return this;
	}

	@Override
	public WidgetAnchor getAnchor() {
		return anchor;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		setX(input.readInt());
		setY(input.readInt());
		setWidth(input.readInt());
		setHeight(input.readInt());
		setAnchor(WidgetAnchor.getAnchorFromId(input.readByte()));
		setVisible(input.readBoolean());
		setPriority(RenderPriority.getRenderPriorityFromId(input.readInt()));
		long msb = input.readLong();
		long lsb = input.readLong();
		this.id = new UUID(msb, lsb);
		setTooltip(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(getX());
		output.writeInt(getY());
		output.writeInt(width);
		output.writeInt(height);
		output.writeByte(getAnchor().getId());
		output.writeBoolean(isVisible());
		output.writeInt(priority.getId());
		output.writeLong(getId().getMostSignificantBits());
		output.writeLong(getId().getLeastSignificantBits());
		PacketUtil.writeString(output, getTooltip());
	}
	
	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public UUID getId() {
		return id;
	}
	@Override
	public Screen getScreen() {
		return screen;
	}
	
	@Override
	public Widget setScreen(Screen screen) {
		return setScreen(null, screen);
	}
	
	@Override
	public Widget setScreen(Plugin plugin, Screen screen) {
		if (this.screen != null && screen != null && screen != this.screen) {
			this.screen.removeWidget(this);
		}
		this.screen = screen;
		this.plugin = plugin;
		return this;
	}
	
	@Override
	public RenderPriority getPriority() {
		return priority;
	}
	
	@Override
	public Widget setPriority(RenderPriority priority) {
		this.priority = priority;
		return this;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public Widget setWidth(int width) {
		this.width = width;
		return this;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public Widget setHeight(int height) {
		this.height = height;
		return this;
	}

	@Override
	public int getX() {
		return X;
	}

	@Override
	public int getY() {
		return Y;
	}

	@Override
	public Widget setX(int pos) {
		this.X = pos;
		return this;
	}

	@Override
	public Widget setY(int pos) {
		this.Y = pos;
		return this;
	}

	@Override
	public Widget shiftXPos(int modX) {
		setX(getX() + modX);
		return this;
	}
	
	@Override
	public Widget shiftYPos(int modY) {
		setY(getY() + modY);
		return this;
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public Widget setVisible(boolean enable) {
		visible = enable;
		return this;
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Widget && other.hashCode() == hashCode();
	}
	
	@Override
	public void onTick() {
		
	}
	
	@Override
	public Widget setTooltip(String t){
		tooltip = t;
		return this;
	}
	
	@Override
	public String getTooltip(){
		return tooltip;
	}
	
	@Override
	public Container getContainer() {
		return container;
	}
	
	@Override
	public void setContainer(Container container) {
		if (this.container != null && container != null && container != this.container) {
			this.container.removeChild(this);
		}
		this.container = container;
		updateOffset();
	}
	
	@Override
	public void updateOffset() {
		if (getContainer() == null) {
			offsetX = 0;
			offsetY = 0;
		} else {
			offsetX = getContainer().getOffsetX();
			offsetY = getContainer().getOffsetY();
		}
	}
}
