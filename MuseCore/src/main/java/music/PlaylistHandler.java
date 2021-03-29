package music;

import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistHandler {

    private static final String ROOT_PATH_PICK = "C:\\music\\playlists\\auto\\";
    private static final String ROOT_PATH = "C:\\music\\playlists\\";
    private static final Map<PLAYLIST_TYPE, List<File>> cache = new HashMap<>();

    public enum PLAYLIST_TYPE {
        deep, //1
        ost, //2
        fury, //3
        auto, //4
        gym, //5
        goodly, //6
        pagan, //7
        dark, //8
        warmup, //9
        rpg, //10
       metal, //11
       finest //12
    }

    public static void playRandom(PLAYLIST_TYPE type) {
        for (int i = 0; i < 12; i++) {
            List<File> fileList = cache.get(type);
            if (!ListMaster.isNotEmpty(fileList)) {
                String path = ROOT_PATH_PICK + type;
                fileList = FileManager.getFilesFromDirectory(path, false);
                cache.put(type, new ArrayList<>(fileList));
            }
            if (playRandom(fileList)) {
                return;
            }
        }
    }

    private static boolean playRandom(List<File> fileList) {
        int randomIndex = RandomWizard.getRandomIndex(fileList);
        try {
            File file = fileList.remove(randomIndex);
            File properFile = !isRootPathAlways()? file : FileManager.getFile(ROOT_PATH + file.getName());
            Desktop.getDesktop().open(properFile);
            System.out.println("-- Playing" +
                    properFile.getPath() +
                    " --");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static boolean isRootPathAlways() {
        return false;
    }
}