package main.music.entity;

import main.content.parameters.G_PARAMS;
import main.entity.type.ObjType;
import main.logic.AT_PROPS;

public class Track extends MusicEntity {

	public Track(ObjType t) {
		super(t);
	}

	public void setPath(String line) {
		setProperty(AT_PROPS.PATH, line, true);
	}

	public String getPath() {
		return getProperty(AT_PROPS.PATH);
	}

	public String getDuration() {
		return getParam(G_PARAMS.DURATION);
	}

	public String getArtist() {
		return getProperty(AT_PROPS.ARTIST);
	}
}