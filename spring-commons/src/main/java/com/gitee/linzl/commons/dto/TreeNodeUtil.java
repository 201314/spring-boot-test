package com.gitee.linzl.commons.dto;

import com.alibaba.fastjson.JSON;
import com.gitee.linzl.commons.constants.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树结点工具类
 *
 * @author linzhenlie
 * @date 2019/8/27
 */
public class TreeNodeUtil {
    /**
     * 递归数据，组装成树结构
     *
     * @param treeList 所有节点数据都是平级，通过pid组装成树结构
     * @return java.util.List<com.gitee.linzl.commons.dto.TreeNode>
     */
    public static List<TreeNode> recursionTree(List<TreeNode> treeList) {
        Map<String, TreeNode> allNode = treeList.stream()
                // 防止多次调用
                .peek(treeNode -> treeNode.clear()).collect(Collectors.toMap(TreeNode::getNodeId, Function.identity()));

        allNode.forEach((key, cNode) -> {
            // 将所有数据，组成有上下级关系的数据，存储在Map中
            TreeNode pNode = allNode.get(cNode.getNodePid());
            if (Objects.nonNull(pNode)) {
                pNode.add(cNode);
            }
        });

        List<TreeNode> finalTreeList = allNode.entrySet().stream().map(map -> map.getValue())
                // 只需要将pid为-1或为null的保存，则完成树结构数据组装
                .filter(node -> GlobalConstants.ROOT_ID.equals(node.getNodeId())
                        || StringUtils.isEmpty(node.getNodePid()))
                .collect(Collectors.toList());
        return finalTreeList;
    }

    /**
     * 递归数据，将树结构还原成平级结构
     * <p>
     * 根结点ID必须是 -1
     */
    public static Map<String, TreeNode> reductionTree(TreeNode node) {
        Map<String, TreeNode> allNode = new HashMap<>(1000);
        if (!GlobalConstants.ROOT_ID.equals(node.getNodeId())) {
            allNode.put(node.getNodeId(), node);
        }
        //根结点下数据
        node.getChildren().stream().forEach(treeNode -> {
            Map<String, TreeNode> childMap = reductionTree(treeNode);
            treeNode.getChildren().clear();
            allNode.putAll(childMap);
        });
        return allNode;
    }

    public static Map<String, TreeNode> reductionTree(List<TreeNode> node) {
        Map<String, TreeNode> allNode = new HashMap<>(1000);

        node.stream().forEach(treeNode -> {
            if (!GlobalConstants.ROOT_ID.equals(treeNode.getNodeId())) {
                allNode.put(treeNode.getNodeId(), treeNode);
            }
        });

        //根结点下数据
        node.stream().filter(treeNode -> !CollectionUtils.isEmpty(treeNode.getChildren())).forEach(treeNode -> {
            Map<String, TreeNode> childMap = reductionTree(treeNode.getChildren());
            treeNode.getChildren().clear();
            allNode.putAll(childMap);
        });
        return allNode;
    }

    public static void main(String[] args) {
        String treeNode = null;
        System.out.println("getLabel:" + Optional.ofNullable(treeNode).orElse(""));
    }
}
