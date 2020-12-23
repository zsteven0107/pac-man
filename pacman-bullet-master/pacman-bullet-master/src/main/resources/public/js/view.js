'use strict';


var app;//app to draw polymorphic shapes on canvas
var intervalID = -1;//id of current interval
var gridSize = 16;
var wallSize = gridSize / 2;
var shi = [[1, 0], [0, 1], [-1, 0], [0, -1]];
var mi = [[-1, -1], [-1, 1], [1, -1], [1, 1]];
var textHight = 30;
var sx = 10;
var sy = 70;
var canvasWidth;
var canvasHeight;


/**
 * Draw and clear line on a the canvas
 * @param canvas  The canvas used to draw a line
 * @returns {{drawLine: drawLine, clear: clear}}
 */
function createApp(canvas) {
    var c = canvas.getContext("2d");
    c.fillStyle = "black";
    c.fillRect(0, 0, canvas.width, canvas.height);
    canvasWidth = canvas.width;
    canvasHeight = canvas.height;

    var drawBackground = function () {
        c.fillStyle = "black";
        c.fillRect(0, 0, canvas.width, canvas.height);
    }

    var drawText = function (startX, startY, str) {
        c.font = "bold 30px Verdana";
        c.fillStyle = "white";
        c.fillText(str, startX, startY);
    }

    var drawLine = function (color, startX, startY, endX, endY) {
        startX += sx;
        endX += sx;
        startY += sy;
        endY += sy;
        c.beginPath();
        c.moveTo(startX, startY);
        c.lineTo(endX, endY);
        c.strokeStyle = color;
        if (color == "blue")
            c.lineWidth = 2;
        else
            c.lineWidth = 4;
        c.stroke();
    };

    var drawEyes = function (x, y) {

        c.beginPath();
        c.arc(x + 4, y + 4, 4, 0, 2 * Math.PI, false);
        c.fillStyle = "white";
        c.fill();
        c.beginPath();
        c.arc(x + 5.5, y + 4, 1.5, 0, 2 * Math.PI, false);
        c.fillStyle = "black";
        c.fill();

        c.beginPath();
        c.arc(x + 14, y + 4, 4, 0, 2 * Math.PI, false);
        c.fillStyle = "white";
        c.fill();
        c.beginPath();
        c.arc(x + 15.5, y + 4, 1.5, 0, 2 * Math.PI, false);
        c.fillStyle = "black";
        c.fill();
    }

    let drawImage = function (imgId, x, y, proportion, angle) {
        let img = document.getElementById(imgId);
        let width = img.getAttribute("width");
        let height = img.getAttribute("height");
        c.save();
        c.translate(x, y);
        c.rotate(angle * Math.PI / 180);
        c.drawImage(img, 0, 0, width * proportion, height * proportion);
        c.restore();
    };


    var drawDot = function (x, y, radius) {
        c.fillStyle = "pink";
        c.beginPath();
        c.arc(x * gridSize + gridSize / 2 + sx, y * gridSize + gridSize / 2 + sy, radius, 0, 2 * Math.PI, false);
        c.closePath();
        c.fill();
    }

    //j -> x, i -> y
    var drawWall = function (map, i, j) {
        var startX = j * gridSize;
        var startY = i * gridSize;
        var neighbor = getNeighbor(map, i, j);
        for (var t = 0; t < neighbor.length; t++) {
            var midX1 = j * gridSize + gridSize / 2;
            var midY1 = i * gridSize + gridSize / 2;
            var midX2 = (j + neighbor[t][1]) * gridSize + gridSize / 2;
            var midY2 = (i + neighbor[t][0]) * gridSize + gridSize / 2;
            var color = map[i][j] == 0 ? "blue" : "yellow";
            drawLine(color, midX1, midY1, midX2, midY2);
        }

        // //special
        // //up
        // if ((i > 0 && map[i - 1][j] == -1) || i == 0) {
        //     drawLine(startX, startY, startX + gridSize, startY);
        // }
        // //right
        // if ((j < map[0].length - 1 && map[i][j + 1] == -1) || j == map[0].length - 1) {
        //     if ((i > 0 && map[i - 1][j] != -2) && (i < map.length - 1 && map[i + 1][j] != -2) || i == 0 || i == map.length - 1)
        //         drawLine(startX + gridSize, startY, startX + gridSize, startY + gridSize);
        // }
        // //down
        // if ((i < map.length - 1 && map[i + 1][j] == -1) || i == map.length - 1) {
        //     drawLine(startX + gridSize, startY + gridSize, startX, startY + gridSize);
        // }
        // //left
        // if ((j > 0 && map[i][j - 1] == -1) || j == 0) {
        //     if ((i > 0 && map[i - 1][j] != -2) && (i < map.length - 1 && map[i + 1][j] != -2) || i == 0 || i == map.length - 1)
        //         drawLine(startX, startY + gridSize, startX, startY);
        // }
    }

    var drawBonus = function (bonus) {
        if (bonus.isVisible == false)
            return;
        if (bonus.type == "small")
            drawDot(bonus.pos.x, bonus.pos.y, 3);
        else if (bonus.type == "big")
            drawDot(bonus.pos.x, bonus.pos.y, 6);
        else if (bonus.type == "fruit")
            drawImage("fruit", bonus.pos.x * gridSize - 5 + sx, bonus.pos.y * gridSize + sy - 4, 1, 0);
    }


    var drawPacman = function (pacman) {
        drawImage(pacman.direction, pacman.pos.x * gridSize - 5 + sx, pacman.pos.y * gridSize + sy - 4, 1, 0);
    }

    var drawGhost = function (ghost) {
        if (ghost.status == "normal")
            drawImage(ghost.color, ghost.pos.x * gridSize - 5 + sx, ghost.pos.y * gridSize + sy - 4, 1, 0);
        else if (ghost.status == "flash" && ghost.isVisible == true)
            drawImage(ghost.status, ghost.pos.x * gridSize - 5 + sx, ghost.pos.y * gridSize + sy - 4, 1, 0);
        else if (ghost.status == "eyes")
            drawEyes(ghost.pos.x * gridSize - 5 + sx, ghost.pos.y * gridSize + sy - 4);
    }

    var drawCover = function () {
        c.fillStyle = "grey";
        c.globalAlpha = 0.5;
        c.fillRect(0, 0, canvas.width, canvas.height);
        c.globalAlpha = 1;
    }

    var clear = function () {
        c.clearRect(0, 0, canvas.width, canvas.height);
    };

    return {
        drawWall: drawWall,
        drawBonus: drawBonus,
        drawPacman: drawPacman,
        drawGhost: drawGhost,
        drawText: drawText,
        drawImage: drawImage,
        drawCover: drawCover,
        drawBackground: drawBackground,
        clear: clear
    }
}

//j -> x, i -> y
function getNeighbor(map, i, j) {
    var neighbor = [];
    for (var t = 0; t < shi.length; t++) {
        var newI = i + shi[t][0];
        var newJ = j + shi[t][1];
        if (newI < 0 || newI >= map.length || newJ < 0 || newJ >= map[0].length || (map[newI][newJ] != 0 && map[newI][newJ] != -1))
            continue;
        neighbor.push(shi[t]);
    }
    if (neighbor.length == 3) {
        var badI = 0;
        var badJ = 0;
        var tmp = [];
        for (var t = 0; t < neighbor.length; t++) {
            tmp.push(neighbor[t]);
            badI += neighbor[t][0];
            badJ += neighbor[t][1];
        }
        neighbor = [];
        for (var t = 0; t < tmp.length; t++) {
            if (tmp[t][0] != badI && tmp[t][1] != badJ)
                neighbor.push(tmp[t]);
        }
    } else if (neighbor.length == 4) {
        neighbor = [];
        for (var t = 0; t < mi.length; t++) {
            var newI = i + mi[t][0];
            var newJ = j + mi[t][1];
            if (newI < 0 || newI >= map.length || newJ < 0 || newJ >= map[0].length || map[newI][newJ] == 0 || map[newI][newJ] == -1)
                continue;
            neighbor = [[mi[t][0], 0], [0, mi[t][1]]];
        }
    }
    return neighbor;
}


/**
 * Setup event handling for buttons
 */
window.onload = function () {
    app = createApp(document.querySelector("canvas"));
    var canvas = document.querySelector("canvas");
    canvas.addEventListener('click', function clicked(e) {
        //e.preventDefault();
        var x = e.clientX;
        var y = e.clientY;

        if (x > canvasWidth / 2 && x < canvasWidth / 2 + 80 && y > canvasHeight / 2 && y < canvasHeight / 2 + 80) {
            console.log("start");
            start();
        }
    }, false);
    document.addEventListener('keydown', keydown);

    init();
    //clearInterval(intervalID);
    //intervalID = setInterval(updateLine, 200);

    // $("#btn-sline").click(function () {
    //     createMovingLine("short");
    // });
    // $("#btn-lline").click(function () {
    //     createMovingLine("long");
    // });
    // $("#btn-switch").click(switchStrategy);
    // $("#btn-reset").click(resetLine);
};

/**
 * Initialize the game.
 */
function init() {
    $.get("/init", function (data) {
        console.log(data);
        draw(data);

        //draw start
        app.drawCover();
        app.drawText(canvasWidth / 2 - 50, canvasHeight / 2 - 60, "new game");
        app.drawImage("start", canvasWidth / 2, canvasHeight / 2, 1, 0);

    }, "json");

}


/**
 * Draw the game.
 */
function draw(data) {

    var event = data.event;
    var life = data.life;
    var difficulty = data.difficulty;
    var score = data.score;
    var pacman = data.pacman;
    var ghosts = data.ghosts;
    var bonuses = data.bonuses;
    var map = data.map;

    //draw background
    app.drawBackground();

    //draw walls
    for (var y = 0; y < map.length; y++) {
        var line = map[y];
        for (var x = 0; x < line.length; x++) {
            //0 -> wall
            if (map[y][x] == 0 || map[y][x] == -1) {
                app.drawWall(map, y, x);
            }
        }
    }

    //draw bonus
    for (var i = 0; i < bonuses.length; i++) {
        //console.log(bonuses[i]);
        app.drawBonus(bonuses[i]);
    }

    //draw ghost
    for (var i = 0; i < ghosts.length; i++) {
        //console.log(ghosts[i]);
        app.drawGhost(ghosts[i]);
    }

    //draw pacman
    app.drawPacman(pacman);

    //draw lifes
    for (var i = 0; i < life - 1; i++) {
        app.drawImage("left", (i * 2 + 2) * gridSize + sx, (map.length + 0.5) * gridSize + sy, 1, 0);
    }


    //draw score
    app.drawText(30, textHight, "score");
    app.drawText(30, textHight + 30, score);

    //draw difficulty
    app.drawText(150, textHight, "difficulty");
    app.drawText(150, textHight + 30, difficulty);

    if (event == "game_over") {
        clearInterval(intervalID);
        intervalID = -1;
        app.drawCover();
        app.drawText(canvasWidth / 2 - 50, canvasHeight / 2 - 60, "restart?");
        app.drawImage("start", canvasWidth / 2, canvasHeight / 2, 1, 0);
    } else if (event == "lose_life") {
        clearInterval(intervalID);
        intervalID = -1;
        app.drawCover();
        app.drawText(canvasWidth / 2 - 50, canvasHeight / 2 - 60, "lose life");
        setTimeout(start, 3000);
    } else if (event == "level_up") {
        clearInterval(intervalID);
        intervalID = -1;
        app.drawCover();
        app.drawText(canvasWidth / 2 - 50, canvasHeight / 2 - 60, "level up");
        setTimeout(start, 3000);
    }


}

// /**
//  * Create a line at a location on the canvas
//  */
// function createMovingLine(kind) {
//     $.get("/line/" + kind, function (data) {
//         if (data != null)
//             app.drawLine(data.startLine.x, data.startLine.y, data.endLine.x, data.endLine.y);
//         if (intervalID < 0) {
//             intervalID = setInterval(updateLine, 200);
//         }
//     }, "json");
//
// }
//
// /**
//  * Move a line on the canvas
//  */
// function switchStrategy() {
//     $.get("/switch", function (data) {
//     }, "json");
// }
//
/**
 * Update on the canvas
 */
function update() {
    $.get("/update", function (data) {
        console.log(data);
        clear();
        draw(data);

    }, "json");

}

/**
 * User Control
 */
function control(direction) {
    console.log(direction);
    $.post("/control/" + direction, function (data) {
        //do nothing
    }, "json");

}

/**
 * start
 */
function start() {
    intervalID = setInterval(update, 100);
}

/**
 * Clear the canvas
 */
function clear() {
    app.clear();
}

/**
 * When user press key.
 */
function keydown(e) {
    if (intervalID != -1) {
        var direction = "";
        var key = e.code;
        console.log(key);
        if (key == "ArrowDown") {
            direction = "down";
        } else if (key == "ArrowUp") {
            direction = "up";
        } else if (key == "ArrowLeft") {
            direction = "left";
        } else if (key == "ArrowRight") {
            direction = "right";
        } else if (key == "KeyD") {
            window.location.replace(document.getElementById("url").value);
        }
        control(direction);
    }
}



