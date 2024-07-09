package com.guimc.fuckpcl

import com.guimc.fuckpcl.utils.WindowUtils
import java.io.File
import java.util.*

/**
 * A library checks Plain Craft Launcher
 * @author guimc, liuli (The UnlegitMC Team)
 */
object PCLChecker {
    /**
     * This method will delete in next version and this can only check PCL title because the minecraft folder path not given
     * @return check result
     */
    @Deprecated("This method will delete in next version", ReplaceWith("fullCheck(mcDir, deleteFolder)", "com.guimc.fuckpcl.PCLChecker.fullCheck"))
    fun runCheck(): Boolean {
        return titleCheck()
    }

    /**
     * run full PCL check
     * @param mcDir minecraft folder path
     * @param deleteFolder delete PCL data folder for next PCL deleted check
     * @return check result
     */
    @JvmOverloads
    fun fullCheck(mcDir: File, deleteFolder: Boolean = true): Boolean {
        // check if there is a window named PCL
        if (titleCheck())
            return true

        // maybe the window not exists like close the window after launched , so we need to check the PCL data folder
        if (folderCheck(mcDir, deleteFolder))
            return true

        // PCL is not exists in the PC
        return false
    }

    /**
     * run PCL title check
     * check if there exists a title name contained "Plain Craft Launcher"
     * @return check result
     */
    fun titleCheck(): Boolean {
        return if (!WindowUtils.isWindows()) {
            false // PCL and the native file only support windows
        } else {
            WindowUtils.findWindow("Plain Craft Launcher") // PCL Title "Plain Craft Launcher 2"
        }
    }

    /**
     * run PCL data folder check
     * @param mcDir minecraft folder path
     * @param deleteFolder delete PCL data folder for next PCL deleted check
     * @return check result
     */
    fun folderCheck(mcDir: File, deleteFolder: Boolean): Boolean {
        require(mcDir.exists()) { "Argument \"mcDir\" is not exists" }
        require(mcDir.isDirectory) { "Argument \"mcDir\" should be a folder" }

        var exists = false
        val pclDataDir = File(mcDir, "PCL")
        if (pclDataDir.exists()) {
            if (deleteFolder)
                deleteFolder(pclDataDir)
            exists=true
        } // me need to delete all folders

        val mcVersionDir = File(mcDir, "versions")
        if (mcVersionDir.exists()) { // I think this should be existed but ...
            Arrays.stream(mcVersionDir.listFiles()).forEach { folder: File? ->
                val pclVersionDataDir = File(folder, "PCL")
                if (pclVersionDataDir.exists()) {
                    if (deleteFolder)
                        deleteFolder(pclVersionDataDir)
                    exists = true
                }
            }
        }
        if(exists)
            return true

        return false
    }

    private fun deleteFolder(folder: File){
        require(folder.exists()) { "Argument \"folder\" is not exists" }
        require(folder.isDirectory) { "Argument \"folder\" should be a folder" }

        folder.listFiles().forEach {
            if(it.isFile){
                it.delete()
            }else if(it.isDirectory){
                deleteFolder(it)
            }
        }

        folder.delete()
    }
}