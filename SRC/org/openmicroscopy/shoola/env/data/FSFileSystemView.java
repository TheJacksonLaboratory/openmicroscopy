/*
 * org.openmicroscopy.shoola.env.data.FSFileSystemView 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.env.data;


//Java imports
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.filechooser.FileSystemView;

//Third-party libraries

//Application-internal dependencies
import omero.grid.FileSet;
import omero.grid.RepositoryListConfig;
import omero.grid.RepositoryPrx;
import omero.model.Image;
import omero.model.IObject;
import omero.model.OriginalFile;
import omero.model.OriginalFileI;
import pojos.DataObject;
import pojos.FileData;
import pojos.ImageData;
import pojos.MultiImageData;


/** 
 * Implementation following Swing FileSystemView.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class FSFileSystemView 
{

	/** Reference to the repositories. */
	private Map<FileData, RepositoryPrx> repositories;
	
	/** The id of the user the directory structure if for. */ 
	private long userID;
	
	/** Default configuration. */
	private RepositoryListConfig config;
	
	/**
	 * Returns the repository corresponding to the passed file.
	 * 
	 * @param file The file to handle.
	 * @return See above.
	 */
    private Entry getRepository(DataObject file)
    {
    	Entry entry;
    	Iterator i;
    	String path;
    	FileData data;
    	String refPath;
    	if (file instanceof ImageData) {
    		ImageData img = (ImageData) file;
    		refPath = img.getPathToFile();
    		if (img.getIndex() >= 0) refPath = img.getParentFilePath();
        	i = repositories.entrySet().iterator();

        	while (i.hasNext()) {
    			entry = (Entry) i.next();
    			data = (FileData) entry.getKey();
    			path = data.getAbsolutePath();
    			if (refPath.startsWith(path)) 
    				return entry;
    		}
    	} else if (file instanceof FileData) {
    		FileData f = (FileData) file;
        	refPath = f.getAbsolutePath();
        	i = repositories.entrySet().iterator();
        	while (i.hasNext()) {
    			entry = (Entry) i.next();
    			data = (FileData) entry.getKey();
    			path = data.getAbsolutePath();
    			if (refPath.startsWith(path)) 
    				return entry;
    		}
    	}
    	
    	return null;
    }
    
    /**
     * Populates the collections of files.
     * 
     * @param files 	The files to handle.
     * @param elements  The elements from the <code>FileSystem</code>
     * @param useFileHiding  Pass <code>true</code> to display the hidden files,
     * 						<code>false</code> otherwise.
     */
    private void populate(FileData root, 
    		Vector<DataObject> files, List<FileSet> elements,
    		boolean useFileHiding)
    {
    	if (elements == null) return;
		Iterator<FileSet> i = elements.iterator();
		File f;
		MultiImageData img;
		Iterator j;
		List<ImageData> components;
		FileSet fs;
		String name;
		int count = 0;
		OriginalFile of;
		List<Image> images;
		List<IObject> usedFiles;
		OriginalFile file = null;
		ImageData image;
		String parentName;
		int index;
		String absolute = root.getAbsolutePath();
		if (useFileHiding) {
		} else {
			while (i.hasNext()) {
				fs = i.next();
				name = fs.fileName;
				System.err.println(name);
				f = new File(name);
				if (!f.isHidden()) {
					count = fs.imageCount;
					usedFiles = fs.usedFiles;
					if (usedFiles.size() > 0) 
						file = (OriginalFile) usedFiles.get(0);
					if (count == 0) {
						if (file == null) {
							of = new OriginalFileI();
							of.setName(omero.rtypes.rstring(name));
							file = of;
						} else {
							file.setPath(omero.rtypes.rstring(
									absolute+file.getPath().getValue()));
						}
						files.addElement(new FileData(file));
					} else {
						images = fs.imageList;
						count = images.size();
						if (count == 1) {
							image = new ImageData(images.get(0));
							image.setName(f.getName());
							image.setPathToFile(f.getAbsolutePath());
							files.addElement(image);
						} else if (count > 1) {
							img = new MultiImageData(file);
							parentName = img.getName();
							j = images.iterator();
							components = new ArrayList<ImageData>();
							index = 0;
							while (j.hasNext()) {
								image = new ImageData((Image) j.next()); 
								image.setParentFilePath(img.getAbsolutePath(), 
										index);
								name = image.getName();
								if (name == null || name.length() == 0) {
									image.setName(parentName+"_"+index);
								}
								components.add(image);
								index++;
							}
							img.setComponents(components);
							files.addElement(img);
						}
					}
				}
			}
		}
    }
   
	/** 
	 * Creates a new instance.
	 * 
	 * @param userID	   The id of the user the directory structure if for.
	 * @param repositories The repositories. Mustn't be <code>null</code>.
	 */
	FSFileSystemView(long userID, Map<FileData, RepositoryPrx> repositories)
	{
		if (repositories == null)
			throw new IllegalArgumentException("No repositories specified.");
		this.userID = userID;
		this.repositories = repositories;
		config = new RepositoryListConfig(1, true, true, false, true);
	}

	/**
	 * Returns the id of the user the directory structure is for.
	 * 
	 * @return See above.
	 */
	public long getUserID() { return userID; }
	
    /**
	 * Overridden to handle <code>FileData</code>.
	 * @see FileSystemView#isRoot(FileData)
	 */
    public boolean isRoot(FileData f)
    {
    	if (f == null) return false;
    	Entry entry;
    	Iterator i = repositories.entrySet().iterator();
    	String path;
    	FileData data;
    	while (i.hasNext()) {
			entry = (Entry) i.next();
			data = (FileData) entry.getKey();
			path = data.getAbsolutePath();
			if (path.equals(f.getAbsolutePath()) && data.getId() == f.getId())
				return true;
		}
    	return false;
    }
    
    /**
	 * Returns the roots.
	 * @see FileSystemView#getRoots()
	 */
    public FileData[] getRoots()
    {
    	FileData[] files = new FileData[repositories.size()];
    	Entry entry;
    	Iterator i = repositories.entrySet().iterator();
    	int index = 0;
    	while (i.hasNext()) {
			entry = (Entry) i.next();
			files[index] = (FileData) entry.getKey();
			index++;
		}
        return files;
    }
    
    /**
     * Registers the passed file. Returns the updated file object.
     * 
     * @param file The file to register.
     * @param userID The id of the owner of the directory to register.
     * @return See above.
     */
    public FileData register(FileData file)
    	throws FSAccessException
    {
    	return null;
    	/*if (file == null) return null;
    	RepositoryPrx proxy = getRepository(file);
    	if (proxy == null) return null;
    	try {
    		//TODO: merge method in I/F
    		IObject object = file.asIObject();
    		IObject r = null;
    		if (object instanceof Image) {
    			Image image = (Image) object;
    			//image.setName(omero.rtypes.rstring(file.getName()));
    			String desc = file.getDescription();
    			if (desc != null && desc.length() > 0)
    				image.setDescription(omero.rtypes.rstring(desc));
    			r = proxy.registerObject(object);
    		} else if (object instanceof OriginalFile) {
    			r = proxy.registerObject(object);
    			file.setRegisteredFile((OriginalFile) r);
    		}
    		//if (r != null) file.setRegisteredFile(r);
    		
		} catch (Exception e) {
			new FSAccessException("Cannot register the file: " +
					""+file.getAbsolutePath(), e);
		}
    	
    	return file;*/
    }
    
    /**
     * Returns the path to the thumbnail.
     * 
     * @param object The object to handle.
     * @return See above.
     * @throws FSAccessException
     */
    public String getThumbnail(DataObject object)
    	throws FSAccessException
    {
    	if (object == null) return null;
    	Entry entry;
    	RepositoryPrx proxy;
    	FileData root;
    	if (object instanceof FileData) {
    		FileData f = (FileData) object;
    		if (f.isDirectory() || f.isHidden())
        		return null;
        	if (!f.getAbsolutePath().contains(".")) return null;
        	entry = getRepository(f);
        	if (entry == null) return null;
        	proxy = (RepositoryPrx) entry.getValue();
        	if (proxy == null) return null;
        	try {
        		return proxy.getThumbnail(f.getAbsolutePath());
    		} catch (Exception e) {
    			new FSAccessException("Cannot retrieve the thumbnail for: " +
    					""+f.getAbsolutePath(), e);
    		}
    	} else if (object instanceof ImageData) {
    		ImageData img = (ImageData) object;
    		String name = img.getPathToFile();
    		int index = img.getIndex();
    		if (index >= 0) name = img.getParentFilePath();
    		entry = getRepository(object);
        	if (entry == null) return null;
        	proxy = (RepositoryPrx) entry.getValue();
        	if (proxy == null) return null;
        	try {
        		if (index >= 0) 
        			return proxy.getThumbnailByIndex(name, index);
        		return proxy.getThumbnail(name);
    		} catch (Exception e) {
    			e.printStackTrace();
    			new FSAccessException("Cannot retrieve the thumbnail for: " +
    					""+name, e);
    		}
    	}
    	return null;
    }
    
    /**
     * Returns the files contained in the passed directory.
     * 
     * @param dir 			The directory to handle.
     * @param useFileHiding Pass <code>true</code> to return the files not
     * 						hidden, <code>false</code> otherwise.
     *  @see FileSystemView#getFiles(FileData, boolean)
     */
    public DataObject[] getFiles(FileData dir, boolean useFileHiding)
    	throws FSAccessException
    {
    	if (dir == null) return null;
    	if (!dir.isDirectory()) return null;
    	Entry entry = getRepository(dir);
    	RepositoryPrx proxy = (RepositoryPrx) entry.getValue();
    	FileData root = (FileData) entry.getKey();
    	if (proxy == null) return null;
    	Vector<DataObject> files = new Vector<DataObject>();
    	try {
    		String s = dir.getAbsolutePath();
    		populate(root, files, proxy.listFileSets(s, config), useFileHiding);
		} catch (Exception e) { 
			new FSAccessException("Cannot retrives the files contained in: " +
					dir.getAbsolutePath(), e);
		}
    	return (DataObject[]) files.toArray(new DataObject[files.size()]);
    }
    
    /**
     * Returns <code>true</code> if the file is hidden, <code>false</code>
     * otherwise.
     * 
     * @return See above.
     */
    public boolean isHiddenFile(FileData f) { return f.isHidden(); }
    
    
}
