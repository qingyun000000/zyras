package com.zy.zyras.group.coord.service.impl;

import com.zy.zyras.token.RasEqualityModeToken;
import cn.whl.commonutils.token.TokenTool;
import com.zy.coordc.CoordServiceUtil;
import com.zy.coordc.enums.NodeType;
import com.zy.coordc.exception.NodeExistExcepiton;
import com.zy.coordc.vo.CreateNodeRequest;
import com.zy.coordc.vo.CreateNodeResponse;
import com.zy.coordc.vo.GetNodeRequest;
import com.zy.coordc.vo.NodeResponse;
import com.zy.coordc.vo.RegistRequest;
import com.zy.coordc.vo.RegistResponse;
import com.zy.zyras.group.coord.CoordRasState;
import org.springframework.stereotype.Service;
import com.zy.zyras.group.coord.service.CoordGroupService;
import com.zy.zyras.token.RasCoordModeToken;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Coord服务
 * @author wuhailong
 */
@Service
public class CoordGroupServiceImpl implements CoordGroupService{
    
    @Override
    public void regist(String group){
        RegistRequest request = new RegistRequest();
        request.setCoordUrl("");
        request.setGroup("ras_" + group);
        try {
            RegistResponse registResponse = CoordServiceUtil.regist(request);
        } catch (NodeExistExcepiton ex) {
            GetNodeRequest request1 = new GetNodeRequest();
            NodeResponse masterNode = CoordServiceUtil.getNode(request1);
            CoordRasState.setMaster(false);
        }
    }
    
    @Override
    public void getMasterRAS(String group, int port){
        CreateNodeRequest request = new CreateNodeRequest();
        request.setCoordUrl("");
        request.setGroup(group);
        request.setNodeType(NodeType.TEMPORARY);
        request.setNodeKey("zyRas_" + group +"_Master");
        request.setNodeValue("RemoteAddr:" + port);
        try {
            CreateNodeResponse createNode = CoordServiceUtil.createNode(request);
            CoordRasState.setMaster(true);
        } catch (NodeExistExcepiton ex) {
            GetNodeRequest request1 = new GetNodeRequest();
            NodeResponse masterNode = CoordServiceUtil.getNode(request1);
            CoordRasState.setMaster(false);
        }
    }
    
    @Override
    public String updateRequestToken(String group) throws Exception{
        
        String createToken = null;
        
        if(CoordRasState.isMaster()){
            createToken = TokenTool.createToken(group, new RasCoordModeToken());
            CreateNodeRequest request = new CreateNodeRequest();
            
            try {
                CreateNodeResponse createNode = CoordServiceUtil.createNode(request);
                
            } catch (NodeExistExcepiton ex) {
                Logger.getLogger(CoordGroupServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            GetNodeRequest request = new GetNodeRequest();
            
            NodeResponse createTokenNode = CoordServiceUtil.getNode(request);
            createToken = createTokenNode.getNodeValue();
        }
        return createToken;
    }
    
    
}
