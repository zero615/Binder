package com.zero.support.binder.compiler;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class RuntimeGenerator {
    public void generateDefault(String pkg, File dir) {

        generate("ArrayCreator", Constant.ARRAY_CREATOR, pkg, dir);
        generate("Binder", Constant.BINDER, pkg, dir);
        generate("BinderCreator", Constant.BINDER_CREATOR, pkg, dir);
        generate("BinderException", Constant.BINDER_EXCEPTION, pkg, dir);
        generate("BinderName", Constant.BINDER_NAME, pkg, dir);
        generate("BinderObject", Constant.BINDER_OBJECT, pkg, dir);
        generate("BinderProxy", Constant.BINDER_PROXY, pkg, dir);
        generate("BinderSerializable", Constant.BINDER_SERIALIZABLE, pkg, dir);
        generate("BinderStub", Constant.BINDER_STUB, pkg, dir);
        generate("ClassHolder", Constant.CLASS_HOLDER, pkg, dir);
        generate("DynamicBinderCreator", Constant.DYNAMIC_BINDER_CREATOR, pkg, dir);
        generate("EntranceParcelCreator", Constant.ENTRANCE_PARCEL_CREATOR, pkg, dir);
        generate("FieldHolder", Constant.FIELD_HOLDER, pkg, dir);
        generate("FileParcelCreator", Constant.FILE_PARCEL_CREATOR, pkg, dir);
        generate("Json", Constant.JSON, pkg, dir);
        generate("ListParcelCreator", Constant.LIST_PARCEL_CREATOR, pkg, dir);
        generate("MapParcelCreator", Constant.MAP_PARCEL_CREATOR, pkg, dir);
        generate("MethodHolder", Constant.METHOD_HOLDER, pkg, dir);
        generate("ParcelCreator", Constant.PARCEL_CREATOR, pkg, dir);
        generate("SerializableParcelCreator", Constant.SERIALIZABLE_PARCEL_CREATOR, pkg, dir);
        generate("SparseArrayParcelCreator", Constant.SPARSE_ARRAY_PARCEL_CREATOR, pkg, dir);
    }

    private void generate(String name, String content, String pkg, File dir) {
        File target = new File(dir, pkg.replace(".", "/") + "/binder/" + name + ".java");
        try {

            FileUtils.writeStringToFile(target, content.replace(Constant.PACKAGE_NAME, pkg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
