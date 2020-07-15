package com.gitee.linzl.commons.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.linzl.commons.constants.GlobalConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * 树结构数据转换
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2018年7月31日
 */
@Setter
@Getter
public class TreeNode {
    /**
     * 树结点ID
     */
    @JSONField(ordinal = 0)
    private String nodeId;
    /**
     * 树结点父ID
     */
    @JSONField(ordinal = 1)
    private String nodePid;
    /**
     * 树结构名称
     */
    @JSONField(ordinal = 2)
    private String label;
    /**
     * 节点的类型
     */
    @JSONField(ordinal = 3)
    private String type;

    /**
     * 是否禁用节点,true表示禁用
     */
    @JSONField(ordinal = 4)
    private boolean disabled = false;

    @JSONField(ordinal = 5)
    private List<TreeNode> children = new LinkedList<>();

    /**
     * 默认为根结点
     */
    public TreeNode() {
        this.nodeId = GlobalConstants.ROOT_ID;
        this.label = GlobalConstants.ROOT_ORG_NAME;
    }

    public TreeNode(String nodeId, String nodePid, String label) {
        this.nodeId = nodeId;
        this.nodePid = nodePid;
        this.label = label;
    }

    public void add(TreeNode node) {
        children.add(node);
    }

    public boolean clear() {
        children.clear();
        return true;
    }
}
