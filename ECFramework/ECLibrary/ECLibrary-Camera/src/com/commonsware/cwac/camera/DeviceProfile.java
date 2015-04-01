/***
  Copyright (c) 2013 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.commonsware.cwac.camera;

import android.hardware.Camera;
import android.os.Build;

public class DeviceProfile {
  private static volatile DeviceProfile SINGLETON=null;

  synchronized public static DeviceProfile getInstance() {
    // android.util.Log.e("DeviceProfile", Build.PRODUCT);
    // android.util.Log.e("DeviceProfile",
    // Build.MANUFACTURER);

    if (SINGLETON == null) {
      if ("occam".equals(Build.PRODUCT)) {
        SINGLETON=new Nexus4DeviceProfile();
      }
      else if ("m7".equals(Build.PRODUCT)
          && "HTC".equalsIgnoreCase(Build.MANUFACTURER)) {
        SINGLETON=new HtcOneDeviceProfile();
      }
      else if (("d2att".equals(Build.PRODUCT) || "d2spr".equals(Build.PRODUCT))
          && "samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
        SINGLETON=new SamsungGalaxyS3DeviceProfile();
      }
      else if ("jflteuc".equals(Build.PRODUCT)) {
        SINGLETON=new SamsungGalaxySGHI337DeviceProfile();
      }
      else if ("gd1wifiue".equals(Build.PRODUCT)) {
        SINGLETON=new SamsungGalaxyCameraDeviceProfile();
      }
      else if ("espressowifiue".equals(Build.PRODUCT)) {
        SINGLETON=new SamsungGalaxyTab2Profile();
      }
      else if ("samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
        SINGLETON=new SamsungDeviceProfile();
      }
      else if ("motorola".equalsIgnoreCase(Build.MANUFACTURER)) {
        if ("XT890_rtgb".equals(Build.PRODUCT)) {
          SINGLETON=new MotorolaRazrIProfile();
        }
        else {
          SINGLETON=new MotorolaDeviceProfile();
        }
      }
      else if ("htc_vivow".equalsIgnoreCase(Build.PRODUCT)) {
        SINGLETON=new DroidIncredible2Profile();
      }
      else if ("C1505_1271-7585".equalsIgnoreCase(Build.PRODUCT)) {
        SINGLETON=new SonyXperiaEProfile();
      }
      else {
        SINGLETON=new DeviceProfile();
      }
    }

    return(SINGLETON);
  }

  public DeviceProfile() {
  }

  public boolean useTextureView() {
    return(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !isCyanogenMod());
  }

  public boolean encodesRotationToExif() {
    return(false);
  }

  public boolean rotateBasedOnExif() {
    return(false);
  }

  public boolean portraitFFCFlipped() {
    return(false);
  }

  public int getMinPictureHeight() {
    return(0);
  }

  public int getMaxPictureHeight() {
    return(Integer.MAX_VALUE);
  }

  public Camera.Size getPreferredPreviewSizeForVideo(int displayOrientation,
                                                     int width,
                                                     int height,
                                                     Camera.Parameters parameters) {
    return(null);
  }

  public boolean doesZoomActuallyWork(boolean isFFC) {
    return(true);
  }

  // based on http://stackoverflow.com/a/9801191/115145
  // and
  // https://github.com/commonsguy/cwac-camera/issues/43#issuecomment-23791446

  private boolean isCyanogenMod() {
    return(System.getProperty("os.version").contains("cyanogenmod") || Build.HOST.contains("cyanogenmod"));
  }

  private static class MotorolaRazrIProfile extends
      MotorolaDeviceProfile {
    public boolean doesZoomActuallyWork(boolean isFFC) {
      return(!isFFC);
    }
  }

  private static class HtcOneDeviceProfile extends DeviceProfile {
    public int getMaxPictureHeight() {
      return(1400);
    }
  }

  private static class Nexus4DeviceProfile extends DeviceProfile {
    public int getMaxPictureHeight() {
      return(720);
    }
  }

  private static class SamsungGalaxyTab2Profile extends DeviceProfile {
    public int getMaxPictureHeight() {
      return(1104);
    }
  }

  private static class SamsungGalaxySGHI337DeviceProfile extends
      DeviceProfile {
    public int getMaxPictureHeight() {
      return(2448);
    }
  }

  public static class FullExifFixupDeviceProfile extends DeviceProfile {
    @Override
    public boolean encodesRotationToExif() {
      return(true);
    }

    @Override
    public boolean rotateBasedOnExif() {
      return(true);
    }
  }

  private static class SamsungDeviceProfile extends
      FullExifFixupDeviceProfile {
  }

  private static class SamsungGalaxyS3DeviceProfile extends
      SamsungDeviceProfile {
    public int getMinPictureHeight() {
      return(1836);
    }
  }

  private static class SamsungGalaxyCameraDeviceProfile extends
      SamsungDeviceProfile {
    public int getMaxPictureHeight() {
      return(3072);
    }
  }

  private static class MotorolaDeviceProfile extends
      FullExifFixupDeviceProfile {
  }

  private static class DroidIncredible2Profile extends DeviceProfile {
    public boolean portraitFFCFlipped() {
      return(true);
    }

    public int getMaxPictureHeight() {
      return(1952);
    }
  }

  private static class SonyXperiaEProfile extends
      FullExifFixupDeviceProfile {
  }
}
