function pas = getPlateAcquisitions(session, varargin)
% GETPLATEACQUISITIONS Retrieve plate acquisition objects from the OMERO server
%
%   pas = getPlateAcquisitions(session) returns all the plateruns owned
%   by the session user in the context of the session group.
%
%   pas = getPlateAcquisitions(session, ids) returns all the plate runs
%   identified by the input ids in the context of the session group.
%
%   pas = getPlateAcquisitions(session, 'owner', ownerId) returns all the
%   plate runs owned by the input owner in the context of the session group.
%
%   pas = getPlateAcquisitions(session, ids, 'owner', ownerId) returns all
%   the plate runs identified by the input ids owned by the input owner in
%   the context of the session group.
%
%   Examples:
%
%      pas = getPlateAcquisitions(session);
%      pas = getPlateAcquisitions(session, 'owner', ownerId);
%      pas = getPlateAcquisitions(session, ids);
%      pas = getPlateAcquisitions(session, ids, 'owner', ownerId);
%
% See also: GETOBJECTS, GETPLATES

% Copyright (C) 2013 University of Dundee & Open Microscopy Environment.
% All rights reserved.
%
% This program is free software; you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation; either version 2 of the License, or
% (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License along
% with this program; if not, write to the Free Software Foundation, Inc.,
% 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

% Input check
ip = inputParser;
ip.addOptional('ids', [], @(x) isempty(x) || (isvector(x) && isnumeric(x)));
ip.KeepUnmatched = true;
ip.parse(varargin{:});

% Delegate unmatched arguments check to getObjects function
unmatchedArgs =[fieldnames(ip.Unmatched)' struct2cell(ip.Unmatched)'];
pas = getObjects(session, 'plateacquisition', ip.Results.ids,...
    unmatchedArgs{:});